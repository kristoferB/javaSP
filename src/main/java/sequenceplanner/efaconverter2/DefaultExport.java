/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sequenceplanner.efaconverter2;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.sourceforge.waters.model.compiler.CompilerOperatorTable;
import net.sourceforge.waters.model.marshaller.JAXBModuleMarshaller;
import net.sourceforge.waters.subject.module.ModuleSubject;
import net.sourceforge.waters.subject.module.ModuleSubjectFactory;
import org.supremica.external.avocades.common.Module;

/**
 *
 * @author Mohammad Reza
 */
public class DefaultExport {
    
    private Module module;

    public DefaultExport(Module iModule){
        this.module = iModule;
    }
    
    public void save(){
        try {
            ModuleSubject moduleSubject = module.getModule();
            moduleSubject.setName(module.getModule().getName());

            String filepath = "";
            JFileChooser fc = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("wmod", "wmod");
            fc.setFileFilter(filter);
            int fileResult = fc.showSaveDialog(null);
            if (fileResult == JFileChooser.APPROVE_OPTION) {
                filepath = fc.getSelectedFile().getAbsolutePath();

                if(!filepath.contains(".wmod"))
                    filepath += ".wmod";
                
                File file = new File(filepath);
                file.createNewFile();
                //Save module to file
                ModuleSubjectFactory factory = new ModuleSubjectFactory();
                JAXBModuleMarshaller marshaller =
                        new JAXBModuleMarshaller(factory,
                        CompilerOperatorTable.getInstance());

                marshaller.marshal(moduleSubject, file);

            }
        } catch (Exception t) {
            System.err.println(t);
        }
    }
    
}
