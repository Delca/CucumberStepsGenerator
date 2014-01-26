package cucumberstepswizard.wizards;

import generator.StepsGenerator;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;

import cucumberstepswizard.Utils;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mpe).
 */

public class SampleNewWizardPage extends WizardPage {

    private Text containerText;

    private Text fileText;

    private int minimumWidth = 351;

    private int maximumFieldLength = 38;

    private List stepsPreview = null;

    private ISelection selection;

    private static String stepsFilePath = "";

    private static String featureFilePath = "";

    /**
     * Constructor for SampleNewWizardPage.
     *
     * @param pageName
     */
    public SampleNewWizardPage(ISelection selection) {
        super("wizardPage");
        setTitle("Cucumber Steps generator");
        setDescription("This wizard will take all the steps defined in the feature file, "
                + "and implement stubs for the steps not already present in the Java file.");
        this.selection = selection;
    }

    /**
     * @see IDialogPage#createControl(Composite)
     */
    @Override
    public void createControl(Composite parent) {
        Composite container = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout();
        container.setLayout(layout);
        layout.numColumns = 3;
        layout.verticalSpacing = 9;
        Label label = new Label(container, SWT.NULL);
        label.setText("&Feature file:");

        fileText = new Text(container, SWT.BORDER | SWT.SINGLE | SWT.FILL);
        fileText.setTextLimit(63);
        fileText.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(org.eclipse.swt.events.MouseEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseDown(org.eclipse.swt.events.MouseEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent arg0) {
                handleBrowse("file");
            }
        });

        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.minimumWidth = minimumWidth;
        fileText.setLayoutData(gd);
        fileText.setEditable(false);

        Button buttonFile = new Button(container, SWT.PUSH);
        buttonFile.setText("Browse...");
        buttonFile.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleBrowse("file");
            }
        });


        label = new Label(container, SWT.NULL);
        label.setText("&Steps file:");

        containerText = new Text(container, SWT.BORDER | SWT.SINGLE | SWT.FILL);
        containerText.addMouseListener(new MouseListener() {

            @Override
            public void mouseUp(org.eclipse.swt.events.MouseEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseDown(org.eclipse.swt.events.MouseEvent arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseDoubleClick(org.eclipse.swt.events.MouseEvent arg0) {
                handleBrowse("container");
            }
        });
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.minimumWidth = minimumWidth;
        containerText.setLayoutData(gd);
        containerText.setEditable(false);
        containerText.setTextLimit(63);


        Button button = new Button(container, SWT.PUSH);
        button.setText("Browse...");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleBrowse("container");
            }
        });

        Button andButCheckBox = new Button(container, SWT.CHECK);
        andButCheckBox.setText("Annotate 'And' and 'But' steps as their Given/When/Then counterpart");
        andButCheckBox.setSelection(StepsGenerator.isRevealAdditionalSteps());
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.minimumWidth = minimumWidth;
        gd.horizontalSpan = 3;
        andButCheckBox.setLayoutData(gd);
        andButCheckBox.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent arg0) {
                StepsGenerator.setRevealAdditionalSteps(!StepsGenerator.isRevealAdditionalSteps());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
                ((Button) arg0.widget).setSelection(StepsGenerator.isRevealAdditionalSteps());
            }
        });

        Button camelCaseBox = new Button(container, SWT.CHECK);
        camelCaseBox.setText("Generate method names in camel case");
        camelCaseBox.setSelection(StepsGenerator.isUseCamelCase());
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.minimumWidth = minimumWidth;
        gd.horizontalSpan = 3;
        camelCaseBox.setLayoutData(gd);
        camelCaseBox.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent arg0) {
                StepsGenerator.setUseCamelCase(!StepsGenerator.isUseCamelCase());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
                ((Button) arg0.widget).setSelection(StepsGenerator.isUseCamelCase());
            }
        });

        Button startMethodNameWithStepCategory = new Button(container, SWT.CHECK);
        startMethodNameWithStepCategory.setText("Prefix the method name with the step category (Given/When/Then/And/But)");
        startMethodNameWithStepCategory.setSelection(StepsGenerator.isStartMethodNameWithStepCategory());
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.minimumWidth = minimumWidth;
        gd.horizontalSpan = 3;
        startMethodNameWithStepCategory.setLayoutData(gd);
        startMethodNameWithStepCategory.addSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent arg0) {
                StepsGenerator.setStartMethodNameWithStepCategory(!StepsGenerator.isStartMethodNameWithStepCategory());
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent arg0) {
                ((Button) arg0.widget).setSelection(StepsGenerator.isStartMethodNameWithStepCategory());
            }
        });

        stepsPreview = new List(container, SWT.FILL | SWT.V_SCROLL | SWT.MULTI | SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.minimumWidth = minimumWidth - 50;
        gd.heightHint = 196;
        gd.horizontalSpan = 3;
        stepsPreview.setLayoutData(gd);
        stepsPreview.setSize(stepsPreview.getParent().getSize().x, stepsPreview.getParent().getSize().y);


        initialize();
        dialogChanged();
        setControl(container);
    }

    /**
     * Tests if the current workbench selection is a suitable container to use.
     */

    private void initialize() {
        stepsFilePath = "";
        featureFilePath = "";
        if (selection != null && selection.isEmpty() == false
                && selection instanceof IStructuredSelection) {
            IStructuredSelection ssel = (IStructuredSelection) selection;
            if (ssel.size() > 1)
                return;
            Object obj = ssel.getFirstElement();
            if (obj instanceof IResource) {
                IFile file;
                if (obj instanceof IFile) {
                    file = (IFile) obj;

                    if (file.getFileExtension().equals("java")) {
                        stepsFilePath = file.getFullPath().toString();
                        containerText.setText(file.getFullPath().toString());
                    }

                    if (file.getFileExtension().equals("feature")) {
                        featureFilePath  = file.getFullPath().toString();
                        fileText.setText(file.getFullPath().toString());
                    }

                    String container = getContainerName();
                    String fileName = getFileName();
                    stepsPreview.removeAll();

                    if (container.length() >= maximumFieldLength) {
                        int length = container.length();
                        String displayedText = "..." + container.substring(length - (maximumFieldLength - 3), length);
                        containerText.setText(displayedText);
                    }

                    if (fileName.length() >= maximumFieldLength) {
                        int length = fileName.length();
                        String displayedText = "..." + fileName.substring(length - (maximumFieldLength - 3), length);
                        fileText.setText(displayedText);
                    }

                }
            }
        }
    }

    /**
     * Uses the standard container selection dialog to choose the new value for
     * the container field.
     */

    private void handleBrowse(String fieldToModify) {
        String result = "";

        FilteredResourcesSelectionDialog dialog = new FilteredResourcesSelectionDialog(getShell(), false, ResourcesPlugin.getWorkspace().getRoot(), IResource.FILE);

        dialog.open();

        result = dialog.getFirstResult().toString().substring(2);

        if (fieldToModify.equals("container")) {
            containerText.setText(result);
            stepsFilePath = result;
        }
        if (fieldToModify.equals("file")) {
            fileText.setText(result);
            featureFilePath = result;
        }
        dialogChanged();
    }

    /**
     * Ensures that both text fields are set.
     */

    private void dialogChanged() {
        String container = getContainerName();
        String fileName = getFileName();
        stepsPreview.removeAll();

        if (container.length() >= maximumFieldLength) {
            int length = container.length();
            String displayedText = "..." + container.substring(length - (maximumFieldLength - 3), length);
            containerText.setText(displayedText);
        }

        if (fileName.length() >= maximumFieldLength) {
            int length = fileName.length();
            String displayedText = "..." + fileName.substring(length - (maximumFieldLength - 3), length);
            fileText.setText(displayedText);
        }

        if ((container == null || container.length() == 0) && fileName != null) {
            updateStatus("Steps file must be specified");
            return;
        }
        if ((fileName == null || fileName.length() == 0) && container != null) {
            updateStatus("Feature file must be specified");
            return;
        }

        testForExtension(container, "java");
        testForExtension(fileName, "feature");
        IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
        IResource stepsFile = root.findMember(new Path(getContainerName()));
        IResource featureResource = root.findMember(new Path(getFileName()));

        if (featureResource == null) {
            updateStatus("The specified feature file cannot be found");
        }

        if (stepsFile == null) {
            updateStatus("The specified steps file cannot be found");
        }

        if (stepsPreview != null) {
            String[] defaultPaths = Utils.getDefaultPathsfromPreferences();
            Set<String> missingsSteps = StepsGenerator.getMissingSteps(featureResource.getLocation().toString(), stepsFile.getLocation().toString(), defaultPaths);

            if (missingsSteps == null || missingsSteps.size() <= 0) {
                updateStatus("No steps can be generated from those files");
                return;
            }
            for (String step : missingsSteps) {
                String toDisplay = step.substring(1, step.length() - 1);;

                int maxLength = 79;

                toDisplay = toDisplay.replace("(\\\\d+)", "[NUMBER]").replace("\\\"([^\\\"]+)\\\"", "[STRING]");

                if (toDisplay.length() >= maxLength) {
                    toDisplay = toDisplay.substring(0, maxLength - 3).concat("...");
                }

                stepsPreview.add(toDisplay);
            }


        }

        //stepsPreview.getParent().pack();

        updateStatus(null);
    }

    private void testForExtension(String container, String extension) {
        int dotLoc = container.lastIndexOf('.');
        if (dotLoc != -1) {
            String ext = container.substring(dotLoc + 1);
            if (ext.equalsIgnoreCase(extension) == false) {
                updateStatus("Steps file extension must be \"." + extension + "\"");
                return;
            }
        }
    }

    private void updateStatus(String message) {
        setErrorMessage(message);
        setPageComplete(message == null);
    }

    public String getContainerName() {
        return stepsFilePath;
    }

    public String getFileName() {
        return featureFilePath;
    }

}
