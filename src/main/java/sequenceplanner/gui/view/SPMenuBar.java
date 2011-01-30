package sequenceplanner.gui.view;

/**
 * Depreciated...
 */
import java.awt.event.ActionListener;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

/**
 * Main menu bar for sequenceplanner.
 * Only graphical if used alone.
 * 
 * Menu layout:
 *  File
 *      Create new OperationView
 *      Create new ResourceView
 *      Exit
 *  Edit
 *      Preferences
 *      AddAllCellsToNewView
 *  Project
 *      Open
 *      Save
 *      Save As
 *      Close
 *  Convert
 *      Save EFA as file (optimized)
 *      Save EFA as file (reset)
 *      Save cost automata as file
 *      Save optimal automaton as file
 *      Identify relations
 *  MP
 *      Print product types and op in model 
 *      EFA for transport planning
 *      Update model after transport planning
 *      EFA for MP supervisor
 *
 * @author qw4z1
 */

class SPMenuBar extends JMenuBar{

    JMenu file, edit, project, convert, mp;
    JMenuItem newOperationView, newResourceView, exit, preferences,addAll,
            open,save,saveAs,close,saveEFAo,saveEFAr,saveCost,saveOptimal,identifyr,
            printProduct,efaForTrans,updateAfterTrans,efaForMP;

    public SPMenuBar(){
        super();
        initMenu();
    }

    private void initMenu() {
        //File menu
        file = new JMenu("File");
        file.add(newOperationView= new JMenuItem("New Operation View"));
        file.add(newResourceView = new JMenuItem("New Resource View"));
        file.add(exit = new JMenuItem("Exit"));
        this.add(file);

        //Edit menu
        edit = new JMenu("Edit");
        edit.add(preferences = new JMenuItem("Preferences"));
        edit.add(addAll = new JMenuItem("Add all cells to new view"));
        this.add(edit);

        //Project menu
        project = new JMenu("Project");
        project.add(open = new JMenuItem("Open"));
        project.add(save = new JMenuItem("Save"));
        project.add(saveAs = new JMenuItem("Save As"));
        project.add(close = new JMenuItem("Close"));
        this.add(project);

        //Convert menu
        convert = new JMenu("Convert");
        convert.add(saveEFAo = new JMenuItem("Save EFA as file (optimized)"));
        convert.add(saveEFAr = new JMenuItem("Save EFA as file (reset)"));
        convert.add(saveCost = new JMenuItem("Save cost automata as file"));
        convert.add(saveOptimal= new JMenuItem("Save optimal automaton as file"));
        convert.add(identifyr = new JMenuItem("Identify relations"));
        this.add(convert);

        //Multiproduct menu
        mp = new JMenu("MultiProduct");
        mp.add(printProduct = new JMenuItem("Print product types and op in model"));
        mp.add(efaForTrans = new JMenuItem("EFA for transport planning"));
        mp.add(updateAfterTrans = new JMenuItem("Update model after transport planning"));
        mp.add(efaForMP = new JMenuItem("EFA for MP supervisor"));
        this.add(mp);
    }

    public void addCreatOpL(ActionListener l){
        newOperationView.addActionListener(l);
    }
}


//OLD SPMENUBAR

//package sequenceplanner.gui.view;
//
//import java.awt.Point;
//import java.awt.event.ActionEvent;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.util.Calendar;
//import javax.swing.AbstractAction;
//import javax.swing.JFileChooser;
//import javax.swing.JMenu;
//import javax.swing.JMenuBar;
//import javax.swing.JMenuItem;
//import javax.swing.SwingUtilities;
//import javax.swing.filechooser.FileFilter;
//
//import net.sourceforge.waters.model.compiler.CompilerOperatorTable;
//import net.sourceforge.waters.model.marshaller.DocumentManager;
//import net.sourceforge.waters.model.marshaller.JAXBModuleMarshaller;
//import net.sourceforge.waters.subject.module.ModuleSubject;
//import net.sourceforge.waters.subject.module.ModuleSubjectFactory;
//import org.supremica.automata.Automata;
//import org.supremica.automata.Automaton;
//import org.supremica.automata.IO.AutomataToXML;
//import org.supremica.automata.IO.ProjectBuildFromWaters;
//import org.supremica.automata.Project;
//import org.supremica.automata.algorithms.AutomataVerifier;
//import org.supremica.automata.algorithms.SynchronizationOptions;
//import org.supremica.automata.algorithms.SynchronizationType;
//import org.supremica.automata.algorithms.VerificationAlgorithm;
//import org.supremica.automata.algorithms.VerificationOptions;
//import org.supremica.automata.algorithms.VerificationType;
//import org.supremica.automata.algorithms.minimization.MinimizationOptions;
//import sequenceplanner.PreferencePane;
//import sequenceplanner.SPContainer;
//import sequenceplanner.efaconverter.IdentifyOpRelations;
//import sequenceplanner.efaconverter.SPtoAutomatonConverter;
//import sequenceplanner.model.ConvertFromXML;
//import sequenceplanner.model.ConvertToXML;
//import sequenceplanner.model.Model;
//import sequenceplanner.model.data.ViewData;
//import sequenceplanner.view.operationView.Constansts;
//import sequenceplanner.xml.SequencePlannerProjectFile;
//
///**
// *MenuBar
// * @author qw4z1
// */
//public class SPMenuBar extends JMenuBar{
//    Model model;
//    	// File, if this project is saved so far
//	File projectFile;
//        SPContainer spc;
//
//    public SPMenuBar(SPContainer spc){
//        this.model = spc.getModel();
//        initMenuPanel();
//        this.spc = spc;
//    }
//
//    	// Filefilter for the project
//	private static final FileFilter filter = new FileFilter() {
//
//		@Override
//		public boolean accept(File f) {
//			return f.getName().toLowerCase().endsWith(".sopx")
//					|| f.isDirectory();
//		}
//
//		@Override
//		public String getDescription() {
//			return "Sequence Planner Project File";
//		}
//	};
//
//    SPMenuBar() {
//        initMenuPanel();
//}
//    private void initMenuPanel() {
//		JMenu file = new JMenu("File");
//		add(file);
//
//		JMenuItem newOperationView = new JMenuItem(new AbstractAction(
//				"Create new OperationView") {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				//spc.createOperationView("Operations view "+ SPContainer.viewCounter);
//			}
//		});
//
//		file.add(newOperationView);
//
//		newOperationView = new JMenuItem(new AbstractAction(
//				"Create new ResourceView") {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				spc.createResourceView(model.getResourceRoot());
//			}
//		});
//
//		file.add(newOperationView);
//
//		newOperationView = new JMenuItem(new AbstractAction("Exit") {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				System.exit(0);
//			}
//		});
//
//		file.add(newOperationView);
//
//		// ///////////////////////////////////////
//		// Start of EDIT menu //
//		// ///////////////////////////////////////
//
//		JMenu edit = new JMenu("Edit");
//		add(edit);
//
//		JMenuItem pref = new JMenuItem(new AbstractAction("Preferences") {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				PreferencePane pref = new PreferencePane();
//				Point p = getParent().getLocation();
//
//				pref.setLocation(p.x + 300, p.y + 250);
//
//				pref.setVisible(true);
//
//			}
//		});
//		edit.add(pref);
//
//		JMenuItem addAll = new JMenuItem(new AbstractAction(
//				"AddAllCellsToNewView") {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//			//	OperationView v = createOperationView("ViewAll");
//			//	v.open(model.getChildren(model.getOperationRoot()));
//			}
//		});
//		edit.add(addAll);
//
//		// ///////////////////////////////////////
//		// End of EDIT menu //
//		// ///////////////////////////////////////
//
//		// ////////////////////////////////////
//		// Start of ProjectMenu //
//		// ////////////////////////////////////
//
//		JMenu project = new JMenu("Project");
//		JMenuItem open = new JMenuItem(new AbstractAction("Open") {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				openModel();
//			}
//		});
//
//		project.add(open);
//
//		JMenuItem save = new JMenuItem(new AbstractAction("Save") {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				saveModel(false);
//			}
//		});
//
//		project.add(save);
//
//		JMenuItem saveAs = new JMenuItem(new AbstractAction("Save as") {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				saveModel(true);
//			}
//		});
//
//		project.add(saveAs);
//
//		JMenuItem close = new JMenuItem(new AbstractAction("Close") {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				System.exit(0);
//			}
//		});
//
//		project.add(close);
//
//		add(project);
//		// ////////////////////////////////////
//		// End of ProjectMenu //
//		// ////////////////////////////////////
//
//		JMenu compile = new JMenu("Convert");
//
//		compile.add(newOperationView = new JMenuItem(new AbstractAction(
//				"Verify nonblocking") {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// The user clicked on Create EFA
//
//				// Create a module of EFAs with Sequence Planner SOP as input
//
//				Runnable t = new Runnable() {
//
//					@Override
//					public void run() {
//						try {
//
//							ProjectBuildFromWaters projectBuilder = new ProjectBuildFromWaters(
//									new DocumentManager());
//							SPtoAutomatonConverter converter = new SPtoAutomatonConverter(
//									model, 1);
//							ModuleSubject moduleSubject = converter.getModule()
//									.getModule();
//							Project efaProject = projectBuilder
//									.build(moduleSubject);
//
//							VerificationOptions vo = new VerificationOptions();
//							vo.setVerificationType(VerificationType.NONBLOCKING);
//							vo.setAlgorithmType(VerificationAlgorithm.MONOLITHIC);
//
//							SynchronizationOptions so = new SynchronizationOptions();
//							so.setSynchronizationType(SynchronizationType.FULL);
//
//							MinimizationOptions mo = new MinimizationOptions();
//
//							AutomataVerifier av = new AutomataVerifier(
//									efaProject, vo, so, mo);
//							Boolean success = av.verify();
//
//							/*JOptionPane.showInternalMessageDialog(
//									SPContainer.this,
//									(success ? "System is Non-blocking"
//											: "System is blocking"));*/
//
//						} catch (Exception t) {
//							t.printStackTrace();
//						}
//					}
//				};
//
//				SwingUtilities.invokeLater(t);
//
//			}
//		}));
//
//		compile.add(newOperationView = new JMenuItem(new AbstractAction(
//				"Save EFA as file (optimization)") {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// The user clicked on Create EFA
//
//				// Create a module of EFAs with Sequence Planner SOP as input
//				try {
//					SPtoAutomatonConverter converter = new SPtoAutomatonConverter(
//							model, 1);
//
//					ModuleSubject moduleSubject = converter.getModule()
//							.getModule();
//					moduleSubject.setName("Sequence Planner to EFA output");
//
//					String filepath = "";
//					JFileChooser fc = new JFileChooser(
//							"C:\\Documents and Settings\\EXJOBB SOCvision\\Desktop");
//					int fileResult = fc.showSaveDialog(null);
//					if (fileResult == JFileChooser.APPROVE_OPTION) {
//						filepath = fc.getSelectedFile().getAbsolutePath();
//
//						File file = new File(filepath);
//
//						file.createNewFile();
//
//						ModuleSubjectFactory factory = new ModuleSubjectFactory();
//
//						// Save module to file
//
//						JAXBModuleMarshaller marshaller = new JAXBModuleMarshaller(
//								factory, CompilerOperatorTable.getInstance());
//
//						marshaller.marshal(moduleSubject, file);
//
//					}
//
//				} catch (Exception t) {
//					t.printStackTrace();
//				}
//
//			}
//		}));
//
//		compile.add(newOperationView = new JMenuItem(new AbstractAction(
//				"Save EFA as file (reset)") {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// The user clicked on Create EFA
//
//				// // Create a module of EFAs with Sequence Planner SOP as input
//				try {
//					SPtoAutomatonConverter converter = new SPtoAutomatonConverter(
//							model, 2);
//
//					ModuleSubject moduleSubject = converter.getModule()
//							.getModule();
//					moduleSubject.setName("Sequence Planner to EFA output");
//
//					String filepath = "";
//					JFileChooser fc = new JFileChooser(
//							"C:\\Documents and Settings\\EXJOBB SOCvision\\Desktop");
//					int fileResult = fc.showSaveDialog(null);
//					if (fileResult == JFileChooser.APPROVE_OPTION) {
//						filepath = fc.getSelectedFile().getAbsolutePath();
//
//						File file = new File(filepath);
//
//						file.createNewFile();
//
//						ModuleSubjectFactory factory = new ModuleSubjectFactory();
//
//						// Save module to file
//
//						JAXBModuleMarshaller marshaller = new JAXBModuleMarshaller(
//								factory, CompilerOperatorTable.getInstance());
//
//						marshaller.marshal(moduleSubject, file);
//
//					}
//
//				} catch (Exception t) {
//					t.printStackTrace();
//				}
//
//			}
//		}));
//
//		compile.add(newOperationView = new JMenuItem(new AbstractAction(
//				"Save cost automata as file") {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// The user clicked on Create EFA
//				try {
//					SPtoAutomatonConverter converter = new SPtoAutomatonConverter(
//							model, 1);
//
//					Automata costAutomata = converter.getCostAutomata();
//
//					String filepath = "";
//					JFileChooser fc = new JFileChooser(
//							"C:\\Documents and Settings\\EXJOBB SOCvision\\Desktop");
//					int fileResult = fc.showSaveDialog(null);
//					if (fileResult == JFileChooser.APPROVE_OPTION) {
//						filepath = fc.getSelectedFile().getAbsolutePath();
//
//						File fil = new File(filepath);
//
//						fil.createNewFile();
//
//						AutomataToXML a = new AutomataToXML(costAutomata);
//
//						a.serialize(filepath);
//
//					} else {
//						System.out.println("User clicked cancel...");
//					}
//				} catch (Exception w) {
//					w.printStackTrace();
//				}
//
//			}
//		}));
//		compile.add(newOperationView = new JMenuItem(new AbstractAction(
//				"Save optimal automaton as file") {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// The user clicked on Create EFA
//
//				try {
//					SPtoAutomatonConverter converter = new SPtoAutomatonConverter(
//							model, 5);
//
//					Automata totalAutomata = converter.getTotalAutomata();
//
//					Automaton optimizedAutomaton = converter
//							.optimizeAutomata(totalAutomata);
//
//					String filepath = "";
//					JFileChooser fc = new JFileChooser(
//							"C:\\Documents and Settings\\EXJOBB SOCvision\\Desktop");
//					int fileResult = fc.showSaveDialog(null);
//					if (fileResult == JFileChooser.APPROVE_OPTION) {
//						filepath = fc.getSelectedFile().getAbsolutePath();
//
//						File fil = new File(filepath);
//
//						fil.createNewFile();
//
//						AutomataToXML a = new AutomataToXML(optimizedAutomaton);
//
//						a.serialize(filepath);
//
//					}
//
//				} catch (Exception q) {
//					q.printStackTrace();
//				}
//
//			}
//		}));
//
//		compile.add(newOperationView = new JMenuItem(new AbstractAction(
//				"Identify Relations") {
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				// The user clicked on Create EFA
//
//				try {
//					IdentifyOpRelations idOp = new IdentifyOpRelations();
//					idOp.identifyRelations(model);
//
//				} catch (Exception q) {
//					q.printStackTrace();
//				}
//
//			}
//		}));
//
//		add(compile);
//	}
////    	public void close() {
////		if (c instanceof AbstractView) {
////			if (((AbstractView) ).closeView()) {
////                            spc.viewMap.removeView();
////                        }
////
////		}
////	}
////
//
//
//	public boolean openModel() {
//		JFileChooser fc = new JFileChooser("user.dir");
//
//		fc.setFileFilter(filter);
//		int answer = fc.showOpenDialog(null);
//
//		if (answer == JFileChooser.APPROVE_OPTION) {
//			closeAllOpenWindows();
//			openModel(fc.getSelectedFile());
//			model.reloadNamesCache();
//			try {
//				ViewData toOpen = (ViewData) model.getViewRoot().getChildAt(0)
//						.getNodeData();
//                                //TODO q fix!!!!
//				spc.createOperationView(toOpen);
//
//			} catch (ClassCastException e) {
//				System.out
//						.println("Could not cast first child of viewroot to viewData");
//			}
//			return true;
//		}
//		return false;
//	}
//
//	public void closeAllOpenWindows() {
////		for (int i = 0; i < spc.viewCounter; i++) {
////			spc.getTabWindow().close();
////
////		}
//
//	}
//
//	public boolean openModel(File inputFile) {
//
//		SequencePlannerProjectFile project = null;
//
//		try {
//			javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext
//					.newInstance(SequencePlannerProjectFile.class.getPackage()
//							.getName());
//			javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx
//					.createUnmarshaller();
//			project = (SequencePlannerProjectFile) unmarshaller
//					.unmarshal(inputFile);
//
//		} catch (javax.xml.bind.JAXBException ex) {
//			java.util.logging.Logger.getLogger("global").log(
//					java.util.logging.Level.SEVERE, null, ex); // NOI18N
//			return false;
//		} catch (ClassCastException ex) {
//			System.out.println("Class Cast Error in openModel");
//			return false;
//		}
//
//		ConvertFromXML con = new ConvertFromXML(model);
//		model = con.convert(project);
//
//		model.rootUpdated();
//
//		return false;
//	}
//
//	// sopx
//	public boolean saveModel(boolean saveAs) {
//
//		if (projectFile == null && !saveAs) {
//			saveAs = true;
//		}
//
//		if (saveAs) {
//			String filepath = "";
//
//			JFileChooser fc = new JFileChooser("user.dir");
//			fc.setFileFilter(filter);
//
//			int fileResult = fc.showSaveDialog(null);
//
//			if (fileResult == JFileChooser.APPROVE_OPTION) {
//				filepath = fc.getSelectedFile().getAbsolutePath();
//
//				filepath = filepath.endsWith(Constansts.FILEFORMAT) ? filepath
//						: filepath + Constansts.FILEFORMAT;
//
//				if (filepath.endsWith(Constansts.FILEFORMAT)) {
//
//					projectFile = saveModelToFile(filepath);
//					return true;
//				}
//			}
//		} else {
//			return saveModelToFile(projectFile);
//		}
//
//		return false;
//	}
//
//	public void saveBackup() {
//		if (projectFile != null) {
//			String path = projectFile.getParent();
//			path = path + File.separatorChar + "backup";
//
//			File f = new File(path);
//			f.mkdir();
//
//			Calendar c = Calendar.getInstance();
//			String date = c.get(Calendar.YEAR) + c.get(Calendar.MONTH)
//					+ c.get(Calendar.DAY_OF_MONTH) + "-"
//					+ c.get(Calendar.HOUR_OF_DAY) + "" + c.get(Calendar.MINUTE)
//					+ "" + c.get(Calendar.SECOND) + "."
//					+ c.get(Calendar.MILLISECOND);
//
//			path = path + File.separatorChar + projectFile.getName() + "_"
//					+ date + Constansts.FILEFORMAT;
//			saveModelToFile(path);
//		}
//	}
//
//	public File saveModelToFile(String filepath) {
//		File file = new File(filepath);
//
//		try {
//			file.createNewFile();
//			saveModelToFile(file);
//			return file;
//
//		} catch (IOException ex) {
//			System.out.println("File save error\n " + ex.getMessage());
//			return null;
//		}
//	}
//
//	public boolean saveModelToFile(File file) {
//		ConvertToXML converter = new ConvertToXML(model);
//		SequencePlannerProjectFile project = converter.convert();
//
//		try {
//			javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext
//					.newInstance(project.getClass().getPackage().getName());
//			javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
//			marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING,
//					"UTF-8"); // NOI18N
//			marshaller.setProperty(
//					javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT,
//					Boolean.TRUE);
//			marshaller.marshal(project, new FileOutputStream(file));
//			return true;
//
//		} catch (javax.xml.bind.JAXBException ex) {
//			throw new RuntimeException("File save error\n " + ex.getMessage(), ex);
//		} catch (FileNotFoundException e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//}
