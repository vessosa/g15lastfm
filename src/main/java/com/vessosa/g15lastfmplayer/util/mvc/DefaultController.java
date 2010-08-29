package com.vessosa.g15lastfmplayer.util.mvc;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Method;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class DefaultController implements PropertyChangeListener {

	private static final Logger LOGGER = Logger.getLogger(DefaultController.class);
	private ArrayList<AbstractView> mRegisteredViews;
	private ArrayList<DefaultModel> mRegisteredModels;

	public DefaultController() {
		this.mRegisteredViews = new ArrayList<AbstractView>();
		this.mRegisteredModels = new ArrayList<DefaultModel>();
	}

	public void addModel(final DefaultModel model) {
		this.mRegisteredModels.add(model);
		model.addPropertyChangeListener(this);
	}

	public void removeModel(final DefaultModel model) {
		this.mRegisteredModels.remove(model);
		model.removePropertyChangeListener(this);
	}

	public void addView(final AbstractView view) {
		this.mRegisteredViews.add(view);
	}

	public void removeView(final AbstractView view) {
		this.mRegisteredViews.remove(view);
	}

	public void propertyChange(final PropertyChangeEvent evt) {
		for (AbstractView view : this.mRegisteredViews) {
			view.modelPropertyChange(evt);
		}
	}

	protected void setModelProperty(final String propertyName, final Object newValue) throws Exception {
		for (DefaultModel model : this.mRegisteredModels) {
			try {
				Method method = model.getClass().getMethod("set" + propertyName, new Class[] { newValue.getClass() });
				method.invoke(model, newValue);
			} catch (NoSuchMethodException e) {
				LOGGER.debug(e);
			}
		}
	}

	protected void callModelAction(final String propertyName) throws Exception {
		for (DefaultModel model : this.mRegisteredModels) {
			try {
				Method method = model.getClass().getMethod("call" + propertyName, new Class[] {});
				method.invoke(model);
			} catch (NoSuchMethodException e) {
				LOGGER.debug(e);
			}
		}
	}

	protected void processModelAction(final String propertyName) throws Exception {
		for (DefaultModel model : this.mRegisteredModels) {
			try {
				Method method = model.getClass().getMethod("process" + propertyName, new Class[] {});
				method.invoke(model);
			} catch (NoSuchMethodException e) {
				LOGGER.debug(e);
			}
		}
	}

	protected void processModelAction(final String propertyName, final Object parameter) throws Exception {
		for (DefaultModel model : this.mRegisteredModels) {
			try {
				Method method = model.getClass().getMethod("process" + propertyName,
						new Class[] { parameter.getClass() });
				method.invoke(model, parameter);
			} catch (NoSuchMethodException e) {
				LOGGER.debug(e);
			}
		}
	}

	protected void callModelAction(final String propertyName, final Object parameter) throws Exception {
		for (DefaultModel model : this.mRegisteredModels) {
			try {
				Method method = model.getClass().getMethod("call" + propertyName, new Class[] { parameter.getClass() });
				method.invoke(model, parameter);
			} catch (NoSuchMethodException e) {
				LOGGER.debug(e);
			}
		}
	}

	protected void callModelAction(final String propertyName, final Object parameter, final Object paramter2)
			throws Exception {
		for (DefaultModel model : this.mRegisteredModels) {
			try {
				Method method = model.getClass().getMethod("call" + propertyName,
						new Class[] { parameter.getClass(), paramter2.getClass() });
				method.invoke(model, parameter, paramter2);
			} catch (NoSuchMethodException e) {
				LOGGER.debug(e);
			}
		}
	}

	// public final boolean isDirty() {
	// for (DefaultModel aModel : this.mRegisteredModels) {
	// if (aModel.isDirty())
	// return true;
	// }
	// return false;
	// }

}
