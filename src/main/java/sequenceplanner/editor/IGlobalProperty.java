/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sequenceplanner.editor;

/**
 *
 * @author Evelina
 */
interface IGlobalProperty {

    public String getName();

    public void setName(String n);

    public String getValue(int i);

    public void setValue(int i, Object value);

    public int getNumberOfValues();

    public int indexOfValue(Object o);
}
