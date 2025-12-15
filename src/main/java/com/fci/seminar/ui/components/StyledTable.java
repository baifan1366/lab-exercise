package com.fci.seminar.ui.components;

import com.fci.seminar.util.UIConstants;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.*;

/**
 * Custom styled table with zebra striping, hover highlighting, and header styling.
 * Follows the UI design guide specifications.
 */
public class StyledTable extends JTable {
    
    private Color rowAltColor;
    private Color hoverColor;
    private int hoveredRow = -1;
    
    /**
     * Creates a styled table with the specified model.
     * @param model The table model
     */
    public StyledTable(TableModel model) {
        super(model);
        initStyle();
    }
    
    /**
     * Creates a styled table with default model.
     */
    public StyledTable() {
        super();
        initStyle();
    }
    
    private void initStyle() {
        this.rowAltColor = UIConstants.TABLE_ROW_ALT;
        this.hoverColor = UIConstants.TABLE_HOVER;
        
        // Basic table settings
        setFont(UIConstants.BODY);
        setRowHeight(UIConstants.TABLE_ROW_HEIGHT);
        setShowGrid(false);
        setIntercellSpacing(new Dimension(0, 0));
        setSelectionBackground(UIConstants.TABLE_HOVER);
        setSelectionForeground(UIConstants.TEXT_PRIMARY);
        setFillsViewportHeight(true);
        
        // Configure header
        configureHeader();
        
        // Set custom renderer for zebra striping
        setDefaultRenderer(Object.class, new StyledTableCellRenderer());
        
        // Add mouse motion listener for hover effect
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                int row = rowAtPoint(e.getPoint());
                if (row != hoveredRow) {
                    hoveredRow = row;
                    repaint();
                }
            }
        });
        
        // Reset hover when mouse exits
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                hoveredRow = -1;
                repaint();
            }
        });
    }
    
    private void configureHeader() {
        JTableHeader header = getTableHeader();
        header.setFont(UIConstants.BODY_BOLD);
        header.setBackground(UIConstants.TABLE_HEADER);
        header.setForeground(UIConstants.TEXT_PRIMARY);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 48));
        header.setReorderingAllowed(false);
        
        // Custom header renderer
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(UIConstants.TABLE_HEADER);
                c.setForeground(UIConstants.TEXT_PRIMARY);
                c.setFont(UIConstants.BODY_BOLD);
                setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, UIConstants.BORDER),
                    BorderFactory.createEmptyBorder(0, 12, 0, 12)
                ));
                setHorizontalAlignment(LEFT);
                return c;
            }
        });
    }
    
    /**
     * Gets the currently hovered row.
     * @return The hovered row index, or -1 if none
     */
    public int getHoveredRow() {
        return hoveredRow;
    }
    
    /**
     * Sets the alternate row color for zebra striping.
     * @param color The alternate row color
     */
    public void setRowAltColor(Color color) {
        this.rowAltColor = color;
        repaint();
    }
    
    /**
     * Sets the hover highlight color.
     * @param color The hover color
     */
    public void setHoverColor(Color color) {
        this.hoverColor = color;
        repaint();
    }
    
    /**
     * Custom cell renderer for zebra striping and hover effects.
     */
    private class StyledTableCellRenderer extends DefaultTableCellRenderer {
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            if (isSelected) {
                c.setBackground(getSelectionBackground());
                c.setForeground(getSelectionForeground());
            } else if (row == hoveredRow) {
                c.setBackground(hoverColor);
                c.setForeground(UIConstants.TEXT_PRIMARY);
            } else {
                // Zebra striping
                c.setBackground(row % 2 == 0 ? Color.WHITE : rowAltColor);
                c.setForeground(UIConstants.TEXT_PRIMARY);
            }
            
            // Add cell padding
            setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
            
            return c;
        }
    }
}
