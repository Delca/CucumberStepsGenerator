package cucumberstepswizard.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import cucumberstepswizard.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

/*
 * (non-Javadoc)
 *
 * @see org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#initializeDefaultPreferences()
 */
@Override
    public void initializeDefaultPreferences() {
IPreferenceStore store = Activator.getDefault().getPreferenceStore();
store.setDefault(PreferenceConstants.PATHS_PREFERENCE, "");
}

}
