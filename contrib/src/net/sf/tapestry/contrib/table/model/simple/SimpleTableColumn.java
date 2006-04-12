package net.sf.tapestry.contrib.table.model.simple;

import java.io.Serializable;
import java.util.Comparator;

import net.sf.tapestry.INamespace;
import net.sf.tapestry.IPage;
import net.sf.tapestry.IRender;
import net.sf.tapestry.IRequestCycle;
import net.sf.tapestry.contrib.table.model.ITableColumn;
import net.sf.tapestry.contrib.table.model.ITableModelSource;
import net.sf.tapestry.valid.RenderString;

/**
 * @version $Id$
 * @author mindbridge
 *
 */
public abstract class SimpleTableColumn implements ITableColumn, Serializable
{
	private String m_strColumnName;
	private String m_strDisplayName;
	private boolean m_bSortable;
	private Comparator m_objComparator;

	/**
	 * Creates a SimpleTableColumn
	 * @param strColumnName the identifying name and display name of the column
	 */
	public SimpleTableColumn(String strColumnName)
	{
		this(strColumnName, strColumnName);
	}

	/**
     * Creates a SimpleTableColumn
	 * @param strColumnName the identifying name and display name of the column
	 * @param bSortable whether the column is sortable
	 */
	public SimpleTableColumn(String strColumnName, boolean bSortable)
	{
		this(strColumnName, strColumnName, bSortable);
	}

	/**
     * Creates a SimpleTableColumn
	 * @param strColumnName the identifying name of the column
	 * @param strDisplayName the display name of the column
	 */
	public SimpleTableColumn(String strColumnName, String strDisplayName)
	{
		this(strColumnName, strDisplayName, false);
	}

	/**
     * Creates a SimpleTableColumn
	 * @param strColumnName the identifying name of the column
	 * @param strDisplayName the display name of the column
	 * @param bSortable whether the column is sortable
	 */
	public SimpleTableColumn(
		String strColumnName,
		String strDisplayName,
		boolean bSortable)
	{
		m_strColumnName = strColumnName;
		m_strDisplayName = strDisplayName;
		m_bSortable = bSortable;

		setComparator(new DefaultComparator());
	}

	/**
	 * @see net.sf.tapestry.contrib.table.model.ITableColumn#getColumnName()
	 */
	public String getColumnName()
	{
		return m_strColumnName;
	}

	/**
	 * Returns the display name of the column that will be used 
     * in the table header.
     * Override for internationalization.
	 * @return String the display name of the column
	 */
	public String getDisplayName()
	{
		return m_strDisplayName;
	}

	/**
	 * @see net.sf.tapestry.contrib.table.model.ITableColumn#getSortable()
	 */
	public boolean getSortable()
	{
		return m_bSortable;
	}

	/**
	 * @see net.sf.tapestry.contrib.table.model.ITableColumn#getComparator()
	 */
	public Comparator getComparator()
	{
		return m_objComparator;
	}

	protected void setComparator(Comparator objComparator)
	{
		m_objComparator = objComparator;
	}

	/**
	 * Extracts the value of the column from the row object
	 * @param objRow the row object
	 * @return Object the column value
	 */
	public Object getColumnValue(Object objRow)
    {
        return objRow.toString();
    }

	/**
	 * @see net.sf.tapestry.contrib.table.model.ITableColumn#getColumnRenderer(IRequestCycle, ITableModelSource)
	 */
	public IRender getColumnRenderer(
		IRequestCycle objCycle,
		ITableModelSource objSource)
	{
		// to be implemented
		//return new RenderString(getDisplayName());
		INamespace objNamespace = objSource.getNamespace();
		String strNamespace = objNamespace.getExtendedId();
		if (strNamespace == null)
			strNamespace = "";
		else
			strNamespace = strNamespace + ":";

		IPage objPage =
			objCycle.getPage(strNamespace + "SimpleTableColumnPage");
		ISimpleTableColumnRenderer objRenderer =
			(ISimpleTableColumnRenderer) objPage.getComponent(
				"tableColumnComponent");
		objRenderer.initializeColumnRenderer(this, objSource);
		return objRenderer;
	}

	/**
	 * @see net.sf.tapestry.contrib.table.model.ITableColumn#getValueRenderer(IRequestCycle, ITableModelSource, Object)
	 */
	public IRender getValueRenderer(
		IRequestCycle objCycle,
		ITableModelSource objSource,
		Object objRow)
	{
        Object objValue = getColumnValue(objRow);
        if (objValue == null)
            objValue = "";

		return new RenderString(objValue.toString());
	}

	private class DefaultComparator implements Comparator, Serializable
	{
		public int compare(Object objRow1, Object objRow2)
		{
			Object objValue1 = getColumnValue(objRow1);
			Object objValue2 = getColumnValue(objRow2);

			if (!(objValue1 instanceof Comparable)
				|| !(objValue2 instanceof Comparable))
			{
				// error
				return 0;
			}

			return ((Comparable) objValue1).compareTo(objValue2);
		}
	}
}