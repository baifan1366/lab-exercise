package com.fci.seminar.util;

import java.awt.Color;
import java.awt.Font;

/**
 * UI Constants for the Seminar Management System.
 * Contains all design tokens including colors, fonts, spacing, and dimensions.
 */
public final class UIConstants {
    
    private UIConstants() {
        // Prevent instantiation
    }
    
    // ===== Color Definitions =====
    
    // Primary Colors
    public static final Color PRIMARY = new Color(25, 55, 109);           // #19376D dark blue
    public static final Color PRIMARY_LIGHT = new Color(87, 108, 188);    // #576CBC light blue
    public static final Color PRIMARY_DARK = new Color(15, 35, 75);       // dark blue variant
    public static final Color ACCENT = new Color(10, 194, 239);           // #0AC2EF accent color
    
    // Background Colors
    public static final Color BG_MAIN = new Color(248, 249, 252);         // #F8F9FC main background
    public static final Color BG_CARD = Color.WHITE;                       // card background
    public static final Color BG_SIDEBAR = new Color(25, 55, 109);        // sidebar background
    public static final Color BG_HEADER = Color.WHITE;                     // header background
    
    // Text Colors
    public static final Color TEXT_PRIMARY = new Color(33, 37, 41);       // #212529 primary text
    public static final Color TEXT_SECONDARY = new Color(108, 117, 125);  // #6C757D secondary text
    public static final Color TEXT_LIGHT = Color.WHITE;                    // light text
    public static final Color TEXT_MUTED = new Color(173, 181, 189);      // #ADB5BD muted text
    
    // Status Colors
    public static final Color SUCCESS = new Color(40, 167, 69);           // #28A745 success
    public static final Color WARNING = new Color(255, 193, 7);           // #FFC107 warning
    public static final Color DANGER = new Color(220, 53, 69);            // #DC3545 danger/error
    public static final Color INFO = new Color(23, 162, 184);             // #17A2B8 info

    // Border Colors
    public static final Color BORDER = new Color(222, 226, 230);          // #DEE2E6
    public static final Color BORDER_LIGHT = new Color(233, 236, 239);    // #E9ECEF
    
    // Table Colors
    public static final Color TABLE_HEADER = new Color(241, 243, 245);    // #F1F3F5
    public static final Color TABLE_ROW_ALT = new Color(248, 249, 250);   // #F8F9FA zebra stripe
    public static final Color TABLE_HOVER = new Color(233, 236, 239);     // #E9ECEF hover
    
    // ===== Font Definitions =====
    
    public static final String FONT_FAMILY = "Segoe UI";
    public static final String FONT_FAMILY_CN = "Microsoft YaHei";
    
    public static final Font TITLE_LARGE = new Font(FONT_FAMILY, Font.BOLD, 24);
    public static final Font TITLE_MEDIUM = new Font(FONT_FAMILY, Font.BOLD, 18);
    public static final Font TITLE_SMALL = new Font(FONT_FAMILY, Font.BOLD, 14);
    public static final Font BODY = new Font(FONT_FAMILY, Font.PLAIN, 13);
    public static final Font BODY_BOLD = new Font(FONT_FAMILY, Font.BOLD, 13);
    public static final Font SMALL = new Font(FONT_FAMILY, Font.PLAIN, 11);
    public static final Font MENU = new Font(FONT_FAMILY, Font.PLAIN, 14);
    
    // ===== Dimension Definitions =====
    
    public static final int SIDEBAR_WIDTH = 220;
    public static final int HEADER_HEIGHT = 60;
    public static final int STATUSBAR_HEIGHT = 30;
    public static final int BUTTON_HEIGHT = 38;
    public static final int INPUT_HEIGHT = 38;
    public static final int TABLE_ROW_HEIGHT = 44;
    
    // ===== Spacing Definitions =====
    
    public static final int SPACING_XS = 4;
    public static final int SPACING_SM = 8;
    public static final int SPACING_MD = 16;
    public static final int SPACING_LG = 24;
    public static final int SPACING_XL = 32;
    
    // ===== Border Radius Definitions =====
    
    public static final int RADIUS_SM = 4;
    public static final int RADIUS_MD = 8;
    public static final int RADIUS_LG = 12;
}
