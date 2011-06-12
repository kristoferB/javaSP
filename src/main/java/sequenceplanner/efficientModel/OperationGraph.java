/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.efficientModel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import net.sourceforge.waters.model.compiler.CompilerOperatorTable;
import net.sourceforge.waters.model.marshaller.JAXBModuleMarshaller;
import net.sourceforge.waters.subject.module.ModuleSubject;
import net.sourceforge.waters.subject.module.ModuleSubjectFactory;
import net.sourceforge.waters.xsd.base.EventKind;
import org.jgrapht.alg.EdmondsKarpMaximumFlow;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.supremica.automata.Arc;
import org.supremica.automata.ArcSet;
import org.supremica.automata.Automaton;
import org.supremica.automata.AutomatonType;
import org.supremica.automata.LabeledEvent;
import org.supremica.automata.State;
import org.supremica.external.avocades.common.EFA;
import org.supremica.external.avocades.common.Module;
import sequenceplanner.model.TreeNode;

/**
 *
 * @author shoaei
 */
public class OperationGraph {

    private Automaton automaton;
    private final Double MAX_CAPACITY = 1.0;
    private List<List<String>> listOfPaths;
    private int mode = 0;

    public OperationGraph(){
        automaton = new Automaton("Graph automaton");
        automaton.setType(AutomatonType.PLANT);
    }

    public void addState(String name){
        automaton.addStateChecked(new State(name));
        //return automaton.getStateWithName(name);
    }

    public void addAllStates(List<String> names){
        for(String name : names)
            automaton.addState(new State(name));
    }

    public void addEdge(String from, String to, String label, double cost){
        LabeledEvent e = new LabeledEvent(label);
        automaton.addArc(new Arc(automaton.getStateWithName(from),
                automaton.getStateWithName(to), e, cost));
        if(!automaton.getAlphabet().contains(e))
            automaton.getAlphabet().addEvent(e);
    }

    public Automaton getGraph(){
        return automaton;
    }

    public List<List<String>> getPaths(){
        return listOfPaths;
    }

    public void calculate(int mode){
        this.mode = mode;
        automaton.getAlphabet().add(new LabeledEvent("start"));
        automaton.getAlphabet().add(new LabeledEvent("finish"));
        automaton.getAlphabet().add(new LabeledEvent("temp"));

        Automaton cloneAutomaton = new Automaton(automaton.clone());
        applyVerticesWeightOne(cloneAutomaton);
        pathIterator(cloneAutomaton);
    }

    private void applyVerticesWeightOne(Automaton automaton){
        Queue<State> queue = new LinkedList<State>();
        for(State state : automaton.getStateSet()){
            queue.add(state);
        }
        while(!queue.isEmpty()){
            State state = queue.poll();
            if(state.nbrOfIncomingArcs() > 1){
                State inState = new State(state.getName()+"_in");
                State outState = new State(state.getName()+"_out");
                Arc ioArc = new Arc(inState, outState, new LabeledEvent("temp"), MAX_CAPACITY);
                automaton.addState(inState);
                automaton.addState(outState);
                automaton.addArc(ioArc);
                ArcSet arcs = new ArcSet();
                for(Iterator itr = state.incomingArcsIterator(); itr.hasNext();){
                    Arc arc = (Arc)itr.next();
                    Arc newarc = new Arc(arc.getSource(), inState, arc.getEvent(), MAX_CAPACITY);
                    automaton.addArc(newarc);
                    arcs.add(arc);
                }
                for(Iterator itr = state.outgoingArcsIterator(); itr.hasNext();){
                    Arc arc = (Arc)itr.next();
                    Arc newarc = new Arc(outState, arc.getTarget(), arc.getEvent(), MAX_CAPACITY);
                    automaton.addArc(newarc);
                    arcs.add(arc);
                }

                for(Arc arc : arcs)
                    automaton.removeArc(arc);

                automaton.removeState(state);
            }
        }
    }

    private void pathIterator(Automaton automaton){
        listOfPaths = new ArrayList<List<String>>();
        boolean flag = true;
        do{
            finalizeDFA(automaton);
            if(mode == 1 & flag){
                flag = false;
                save(automaton);
            }else if(mode == 2){
                save(automaton);
            }
            //reductVertices(automaton);
            setArcsCapacity(automaton,MAX_CAPACITY);
            calculateFlowMax(automaton);
            List<List<String>> paths = getPaths(cloneit(automaton));
            listOfPaths.addAll(paths);
            removeStatesByArcCapacity(automaton, MAX_CAPACITY);
        } while(automaton.getStateSet().size() != 0);
    }

    private void finalizeDFA(Automaton automaton) {

        State init = automaton.addStateChecked(new State("ProjectStart"));
        init.setInitial(true);
        init.setAccepting(true);

        State finish = automaton.addStateChecked(new State("ProjectFinish"));
        finish.setAccepting(true);

        for(State state : automaton.getStateSet()){
            if(!(state.getName().matches("ProjectFinish") || state.getName().matches("ProjectStart")) ){
                if(state.nbrOfIncomingArcs() == 0 && state.nbrOfOutgoingArcs() == 0){
                    automaton.addArc(new Arc(init, state, new LabeledEvent("start"), MAX_CAPACITY));
                    automaton.addArc(new Arc(state, finish, new LabeledEvent("finish"), MAX_CAPACITY));
                } else if (state.nbrOfOutgoingArcs() == 0) {
                    automaton.addArc(new Arc(state, finish, new LabeledEvent("finish"), MAX_CAPACITY));
                } else if(state.nbrOfIncomingArcs() == 0){
                    automaton.addArc(new Arc(init, state, new LabeledEvent("start"), MAX_CAPACITY));
                }
            }
        }
    }

    private void setArcsCapacity(Automaton automaton, Double c) {
        for(Iterator itr = automaton.arcIterator(); itr.hasNext();)
            ((Arc)itr.next()).setProbability(c);
    }

    private void calculateFlowMax(Automaton automaton) {

        DirectedWeightedMultigraph<Integer, DefaultWeightedEdge> simple =
            new DirectedWeightedMultigraph<Integer, DefaultWeightedEdge>(
            DefaultWeightedEdge.class);

        List<State> map = new ArrayList<State>();

        int source = 0;
        int sink = 0;

        for(State state : automaton.getStateSet()){
            map.add(state);
            simple.addVertex(map.indexOf(state));
            if(state.isAccepting())
                if(state.isInitial())
                    source=map.indexOf(state);
                else
                    sink=map.indexOf(state);
        }

        for(Iterator itr = automaton.arcIterator(); itr.hasNext();){
            Arc arc = (Arc)itr.next();
            DefaultWeightedEdge e = simple.addEdge(map.indexOf(arc.getSource()), map.indexOf(arc.getTarget()));
            simple.setEdgeWeight(e, arc.getProbability());
        }

        EdmondsKarpMaximumFlow<Integer, DefaultWeightedEdge> solver =
            new EdmondsKarpMaximumFlow<Integer, DefaultWeightedEdge>(simple);

        solver.calculateMaximumFlow(source,sink);
        Map<DefaultWeightedEdge,Double> result = solver.getMaximumFlow();

        for(DefaultWeightedEdge e : result.keySet()){
            int tail = ((Number)e.getSource()).intValue();
            int head = ((Number)e.getTarget()).intValue();
            for(Iterator itr = automaton.arcIterator(); itr.hasNext();){
                Arc arc = (Arc)itr.next();
                if(map.indexOf(arc.getSource()) == tail && map.indexOf(arc.getTarget()) == head){
                    arc.setProbability(result.get(e));
                }
            }
        }
    }

    private List<List<String>> getPaths(Automaton automaton) {
        List<List<String>> list = new ArrayList<List<String>>();
        removeArcsByCapacity(automaton, 0.0);
        //save(automaton);
        State initial = automaton.getInitialState();
        for(Iterator itr = initial.outgoingArcsIterator(); itr.hasNext();){
            List<String> path = new ArrayList<String>();
            Arc arc = (Arc)itr.next();
            State currentState = arc.getTarget();
            boolean finish = false;
            while(!finish){
                String name = currentState.getName();
                if(name.contains("_in")){
                    name = name.substring(0, name.length()-3);
                } else if (name.contains("_out")){
                    name = name.substring(0, name.length()-4);
                }

                if(!path.contains(name)){
                    path.add(name);
                }
                State nextState = currentState.nextStateIterator().next();

                if(!nextState.isAccepting())
                    currentState = nextState;
                else
                    finish = true;
            }
            list.add(path);
        }
        return list;
    }

    private void removeArcsByCapacity(Automaton automaton, double c) {
        List<Arc> removableArcs = new ArrayList<Arc>();
        for(Iterator itr = automaton.arcIterator(); itr.hasNext();){
            Arc arc = (Arc)itr.next();
            if(arc.getProbability() == c)
                removableArcs.add(arc);
        }
        for(Arc arc : removableArcs)
            automaton.removeArc(arc);

        removeAloneStates(automaton);
    }

    private void removeAloneStates(Automaton automaton){
        for(Iterator itr = automaton.safeStateIterator(); itr.hasNext();){
            State state = (State)itr.next();
            if(state.nbrOfIncomingArcs() == 0 && state.nbrOfOutgoingArcs() == 0)
                automaton.removeState(state);
        }
    }

    private Automaton cloneit(Automaton in){
        Automaton out = new Automaton(in.getName());

        for(State state : in.getStateSet()){
            State newstate = new State(state);
            if(state.isAccepting())
                newstate.setAccepting(true);
            else if(state.isInitial())
                newstate.setInitial(true);

            out.addState(newstate);
        }
        for(Iterator itr = in.arcIterator(); itr.hasNext();){
            Arc arc = (Arc)itr.next();
            State tail = out.getStateWithName(arc.getSource().getName());
            State head = out.getStateWithName(arc.getTarget().getName());
            out.addArc(new Arc(tail, head, arc.getEvent(), arc.getProbability()));
        }
        return out;
    }

    private void removeStatesByArcCapacity(Automaton automaton, double c) {
        //StateSet states = new StateSet();
        HashSet<State> states = new HashSet<State>();

        for(Iterator itr = automaton.arcIterator(); itr.hasNext();){
            Arc arc = (Arc)itr.next();
            if(arc.getProbability() == c){
                states.add(arc.getSource());
                states.add(arc.getTarget());
            }

        }
        for(State state : states){
            automaton.removeState(automaton.getStateWithName(state.getName()));
        }
    }

    private void save(Automaton automaton){
        Module module = new Module("Operation Graph", false);
        EFA efa = new EFA("Graph", module);
        convertDFAtoEFA(automaton, efa);
        module.addAutomaton(efa);
        saveToFile(module);
    }

    private void saveToFile(Module module){
        try {
            ModuleSubject moduleSubject = module.getModule();
            moduleSubject.setName("Flatten SOPs Graph");

            String filepath = "";
            JFileChooser fc = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                                                    "wmod", "wmod");
            fc.setFileFilter(filter);
            int fileResult = fc.showSaveDialog(null);
            if (fileResult == JFileChooser.APPROVE_OPTION) {
                filepath = fc.getSelectedFile().getAbsolutePath();

                if(!filepath.contains(".wmod"))
                    filepath += ".wmod";

                File file = new File(filepath);

                //file.createNewFile();

                ModuleSubjectFactory factory = new ModuleSubjectFactory();

                //Save module to file

                JAXBModuleMarshaller marshaller =
                        new JAXBModuleMarshaller(factory,
                        CompilerOperatorTable.getInstance());

                marshaller.marshal(moduleSubject, file);

            }


        } catch (Exception t) {
            System.err.println(t);
        }
    }

    private void convertDFAtoEFA(Automaton automaton, EFA efa) {
        for(State state : automaton.getStateSet())
            //if(!(state.nbrOfIncomingArcs()==0 && state.nbrOfOutgoingArcs()==0))
                efa.addState(state.getName(), state.isAccepting(), state.isInitial());

        for(Iterator<Arc> itr = automaton.arcIterator(); itr.hasNext();){
            Arc arc = itr.next();
            efa.addTransition(arc.getSource().getName(), arc.getTarget().getName(), arc.getLabel(), "", "");
        }
        for(Iterator<LabeledEvent> itr = automaton.eventIterator(); itr.hasNext();)
            efa.addEvent(itr.next().getLabel(), EventKind.CONTROLLABLE.toString());
    }
}
