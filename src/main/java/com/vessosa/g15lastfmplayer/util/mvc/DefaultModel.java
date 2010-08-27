package com.vessosa.g15lastfmplayer.util.mvc;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class DefaultModel {

	protected PropertyChangeSupport mChangeSupport;

	public DefaultModel() {
		this.mChangeSupport = new PropertyChangeSupport(this);
	}

	public void addPropertyChangeListener(final PropertyChangeListener changeListener) {
		this.mChangeSupport.addPropertyChangeListener(changeListener);
	}

	public void removePropertyChangeListener(final PropertyChangeListener changeListener) {
		this.mChangeSupport.removePropertyChangeListener(changeListener);
	}

	public void firePropertyChange(final String propertyName, final Object oldValue, final Object newValue) {
		this.mChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

}
