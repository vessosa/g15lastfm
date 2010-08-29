package com.vessosa.g15lastfmplayer.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.pushingpixels.substance.api.SubstanceLookAndFeel;
import org.pushingpixels.substance.api.skin.TwilightSkin;

import com.vessosa.g15lastfmplayer.G15LastfmPlayer;
import com.vessosa.g15lastfmplayer.controller.Controller;
import com.vessosa.g15lastfmplayer.util.mvc.AbstractView;

public class RadioSearchScreen extends JDialog implements AbstractView {

	private static final long serialVersionUID = 1L;
	private JButton searchButton;
	private JTextField radioField;
	private Controller controller;
	private List<String> radioHistory;

	public RadioSearchScreen(Controller controller) {
		this.controller = controller;
		radioHistory = new ArrayList<String>();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				initGUI();
			}
		});
	}

	private void initGUI() {

		setTitle("G15LastfmPlayer");
		MigLayout layout = new MigLayout("", "grow");
		setLayout(new BorderLayout());
		JPanel main = new JPanel();
		main.setLayout(layout);
		main.add(new JLabel("Type a radio to search:"), "span, wrap");
		main.add(getRadioField(), "grow");
		main.add(getSearchButton(), "wrap");

		add(main);
		setSize(257, 94);
		setLocationRelativeTo(null);
		setResizable(false);
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);

		String actionName = "VK_ESCAPE";
		Action action = new AbstractAction(actionName) {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		};
		main.getActionMap().put(actionName, action);
		main.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
				KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0), actionName);

	}

	@Override
	public void modelPropertyChange(final PropertyChangeEvent evt) {
		final String propertyName = evt.getPropertyName();
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				if (propertyName.equals(Controller.SHOW_SEARCH_DIALOG)) {
					getRadioField().selectAll();
					setVisible(true);
				} else if (propertyName.equals(Controller.POPULATE_RADIO_NAMES)) {
					@SuppressWarnings("unchecked")
					List<String> newRadioList = (List<String>) evt.getNewValue();
					for (String newRadio : newRadioList) {
						if (!radioHistory.contains(newRadio))
							radioHistory.add(newRadio);
					}
					AutoCompleteDecorator.decorate(getRadioField(), radioHistory, false);
				}
			}

		});

	}

	public static void main(String[] args) {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				SubstanceLookAndFeel.setSkin(new TwilightSkin());
			}

		});
		RadioSearchScreen radio = new RadioSearchScreen(new Controller());
		radio.setVisible(true);
	}

	public JButton getSearchButton() {
		if (searchButton == null) {
			searchButton = new JButton("Search");
			searchButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					if (getRadioField().getText().length() > 0) {
						try {
							if (getRadioField().getText().toLowerCase().equals("exit"))
								G15LastfmPlayer.exitApplication();
							if (getRadioField().getText().toLowerCase().equals("update")) {
								G15LastfmPlayer.registerMediaKeys();
								controller.checkUpdate();
							} else {
								addToHistory(getRadioField().getText());
								controller.searchAndPlay(getRadioField().getText());
							}
							getRadioField().selectAll();
							setVisible(false);
						} catch (Exception e) {
							JOptionPane.showMessageDialog(null, e.getCause().getMessage(), "G15Lastfm Player",
									JOptionPane.ERROR_MESSAGE);
						}
					}

				}

			});
		}
		return searchButton;
	}

	private void addToHistory(String text) {
		if (!radioHistory.contains(text)) {
			radioHistory.add(text);
			AutoCompleteDecorator.decorate(getRadioField(), radioHistory, false);
		}
	}

	public JTextField getRadioField() {
		if (radioField == null) {
			radioField = new JTextField();
			radioField.setPreferredSize(new Dimension(400, radioField.getHeight()));
			radioField.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					getSearchButton().doClick();
				}
			});
		}
		return radioField;
	}

}
