package sequenceplanner.weightNonBlocking;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import sequenceplanner.algorithm.AAlgorithm;
import sequenceplanner.algorithm.IAlgorithmListener;
import sequenceplanner.weightNonBlocking.xml.BlockType;
import sequenceplanner.weightNonBlocking.xml.ResourceType;
import sequenceplanner.weightNonBlocking.xml.SeamAssemblyType;
import sequenceplanner.weightNonBlocking.xml.SeamType;

/**
 * DARPA
 * @author patrik
 */
public class XMLInteraction extends AAlgorithm {

    private File mFileXML;

    public XMLInteraction(final IAlgorithmListener iListener) {
        super("XMLInteraction");
        addAlgorithmListener(iListener);
    }

    @Override
    public void init(List<Object> iList) {
        mFileXML = (File) iList.get(0);
    }

    @Override
    public void run() {
        try {
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(SeamAssemblyType.class.getPackage().getName());
            javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            final SeamAssemblyType seamAssemblyType = (SeamAssemblyType) unmarshaller.unmarshal(mFileXML);

            //Create internal data structures
            buildInternalDataStructure(seamAssemblyType);

        } catch (javax.xml.bind.JAXBException ex) {
            java.util.logging.Logger.getLogger("global").log(
                    java.util.logging.Level.SEVERE, null, ex); // NOI18N
        } catch (ClassCastException ex) {
            System.out.println("Class Cast Error in openModel");
        }
        return;
    }

    void buildInternalDataStructure(final SeamAssemblyType iSeamAssemblyType) {
        final List<Object> returnList = new ArrayList<Object>();

        //Resource with payload
        final ResourceType resourceType = iSeamAssemblyType.getResourcesType().getResourceType();
        final Resource crane = new Resource(resourceType.getPayload().doubleValue());

        //Create blocks
        final Map<String, Block> idBlockMap = new HashMap<String, Block>();
        for (final BlockType blockType : iSeamAssemblyType.getBlocksType().getBlockType()) {

            //Weight is not required, so max weight is assumed if no weight is given
            double weight = crane.mPayload;
            if (blockType.getWeight() != null) {
                weight = blockType.getWeight().doubleValue();
            }
            idBlockMap.put(blockType.getId(), new Block(weight, blockType.getName()));
        }

        //Create seams
        final Set<Seam> seamSet = new HashSet<Seam>();
        for (final SeamType seamType : iSeamAssemblyType.getSeamsType().getSeamType()) {
            final String toAdd = seamType.getToadd();
            final String addTo = seamType.getAddto();

            if (!idBlockMap.containsKey(toAdd) || !idBlockMap.containsKey(addTo)) {
                fireNewMessageEvent(seamType.getId() + " contains block not defined in file!");
                return;
            }

            seamSet.add(new Seam(idBlockMap.get(toAdd), idBlockMap.get(addTo)));
        }

        //Send out finished message
        returnList.add(seamSet);
        returnList.add(crane);
        fireFinishedEvent(returnList);
    }
}
