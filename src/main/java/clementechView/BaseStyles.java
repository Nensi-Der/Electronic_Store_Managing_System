package clementechView;

import java.io.InputStream;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;

public final class BaseStyles {

    private BaseStyles() {
    }

    public static final String COLOR_BG = "#FFFFFF";
    public static final String COLOR_HEADER = "#F98E02";
    public static final String COLOR_PRIMARY_BTN = "#51B403";
    public static final String COLOR_PRIMARY_BTN_HOVER = "#6BD10D"; // lighter hover
    public static final String COLOR_PRIMARY_BTN_BORDER = "#1C5901";


    public static final Color BG = Color.web("#FFFFFF");            // page background
    public static final Color SURFACE = Color.web("#FFFFFF");       // white cards

    public static final Color TEXT = Color.web("#1F2937");          // dark text
    public static final Color MUTED_TEXT = Color.web("#6B7280");    // gray text

    public static final Color BORDER = Color.web("#E5E7EB");        // light border
    public static final Color DANGER = Color.web("#DC2626");

    public static final String FONT_RESOURCE = "/fonts/9SYSTEMA.TTF";

    public static final String COLOR_SIDENAV_BG = "#1f2d3d";
    public static final String COLOR_SIDENAV_TEXT = "rgba(255,255,255,0.85)";
    public static final String COLOR_SIDENAV_TEXT_MUTED = "rgba(255,255,255,0.65)";
    public static final String COLOR_SIDENAV_HOVER_BG = "rgba(255,255,255,0.10)";
    public static final String COLOR_SIDENAV_SELECTED_BG = "rgba(255,255,255,0.08)";

    private static final String NAV_SELECTED_KEY = "navSelected";

    private static final Font FONT_12 = loadFont(12);
    public static final String FONT_FAMILY = (FONT_12 != null) ? FONT_12.getFamily() : Font.getDefault().getFamily();

    public static void loadAppFonts() {
        String fontPath = "/fonts/9SYSTEMA.TTF";
    }

    public static final double RADIUS = 16;

    public static final Insets PAGE_PADDING = new Insets(18);
    public static final Insets CARD_PADDING = new Insets(16);

    public static final double FONT_TITLE = 24;
    public static final double FONT_BODY = 14;
    public static final double FONT_SMALL = 12;

    public static void applyRootBackground(Region root) {
        root.setBackground(new Background(new BackgroundFill(
                BG, CornerRadii.EMPTY, Insets.EMPTY
        )));
    }

    public static VBox buildSideNav(String titleText, Label... navItems) {
        VBox sideNav = new VBox(8);
        sideNav.setPadding(new Insets(18, 12, 18, 12));
        sideNav.setPrefWidth(200);
        sideNav.setMinWidth(190);
        sideNav.setMaxWidth(220);

        sideNav.setStyle("""
                -fx-background-color: %s;
                """.formatted(COLOR_SIDENAV_BG));

        Label title = new Label(titleText == null ? "Menu" : titleText);
        styleSideNavTitle(title);

        sideNav.getChildren().add(title);

        if (navItems != null) {
            for (Label item : navItems) {
                if (item == null) continue;
                installSideNavItemStyle(item);
                sideNav.getChildren().add(item);
            }
        }

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        sideNav.getChildren().add(spacer);

        return sideNav;
    }

    public static void styleOrangeTitle(Label label) {
        styleOrangeTitle(label, 28);
    }
    public static void styleOrangeTitle(Label label, double size) {
        if (label == null) return;
        try (java.io.InputStream is = BaseStyles.class.getResourceAsStream("/fonts/9SYSTEMA.TTF")) {
            if (is != null) javafx.scene.text.Font.loadFont(is, 12);
        } catch (Exception ignored) {}
        label.setStyle("""
            -fx-font-family: '%s';
            -fx-font-size: %spx;
            -fx-font-weight: 800;
            -fx-text-fill: %s;
            """.formatted("9SYSTEMA", size, COLOR_HEADER));
    }


    public static void styleSideNavTitle(Label l) {
        l.setStyle("-fx-text-fill: " + COLOR_SIDENAV_TEXT_MUTED + "; -fx-font-weight: 700;");
        l.setFont(font(14));
    }


    public static void installSideNavItemStyle(Label label) {
        label.setCursor(Cursor.HAND);
        label.setPadding(new Insets(10, 10, 10, 12));
        label.setFont(font(15));

        // default not selected
        label.getProperties().putIfAbsent(NAV_SELECTED_KEY, Boolean.FALSE);
        applySideNavItemStyle(label);

        label.setOnMouseEntered(e -> label.setStyle("""
                -fx-text-fill: white;
                -fx-font-weight: 700;
                -fx-background-color: %s;
                -fx-background-radius: 10;
                -fx-padding: 10 10 10 12;
                """.formatted(COLOR_SIDENAV_HOVER_BG)));

        label.setOnMouseExited(e -> applySideNavItemStyle(label));
    }

     //Set which nav item is selected
     //Psh: BaseStyles.setSideNavSelected(navCheckout, navMainMenu, navCheckout);
    public static void setSideNavSelected(Label selected, Label... allItems) {
        if (allItems == null) return;

        for (Label l : allItems) {
            if (l == null) continue;
            boolean isSel = (l == selected);
            l.getProperties().put(NAV_SELECTED_KEY, isSel);
            applySideNavItemStyle(l);
        }
    }

    private static void applySideNavItemStyle(Label label) {
        boolean selected = Boolean.TRUE.equals(label.getProperties().get(NAV_SELECTED_KEY));

        if (selected) {
            label.setStyle("""
                    -fx-text-fill: white;
                    -fx-font-weight: 700;
                    -fx-background-color: %s;
                    -fx-background-radius: 10;
                    -fx-padding: 10 10 10 12;
                    """.formatted(COLOR_SIDENAV_SELECTED_BG));
        } else {
            label.setStyle("""
                    -fx-text-fill: %s;
                    -fx-font-weight: 600;
                    -fx-background-color: transparent;
                    -fx-background-radius: 10;
                    -fx-padding: 10 10 10 12;
                    """.formatted(COLOR_SIDENAV_TEXT));
        }
    }

    public static VBox buildWhitePanel(Node... children) {
        VBox box = new VBox(12);
        box.setPadding(new Insets(18));
        box.setStyle("-fx-background-color: white;");
        if (children != null) box.getChildren().addAll(children);
        return box;
    }

    public static void styleRightPanel(Region panel) {
        panel.setStyle("""
                -fx-background-color: white;
                -fx-border-color: rgba(0,0,0,0.08);
                -fx-border-width: 0 0 0 1;
                """);
    }

    public static void styleSearchField(TextField tf) {
        tf.setPrefHeight(42);
        tf.setFont(font(15));
        tf.setStyle("""
                -fx-background-color: rgba(0,0,0,0.04);
                -fx-background-radius: 12;
                -fx-padding: 10 12 10 12;
                -fx-border-color: rgba(0,0,0,0.10);
                -fx-border-radius: 12;
                -fx-border-width: 1;
                """);
    }


    public static HBox buildSearchRow(TextField tf, Button searchBtn, String prompt, Runnable onSearch) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);

        tf.setPromptText(prompt == null ? "Search..." : prompt);
        styleSearchField(tf);
        HBox.setHgrow(tf, Priority.ALWAYS);

        // You can choose green or primary here; your BillView uses green:
        styleGreenPrimaryButton(searchBtn);
        searchBtn.setMaxWidth(140);
        searchBtn.setPrefWidth(140);

        if (onSearch != null) {
            searchBtn.setOnAction(e -> onSearch.run());
            tf.setOnAction(e -> onSearch.run());
        }

        row.getChildren().addAll(tf, searchBtn);
        return row;
    }

    public static void styleStandardTable(TableView<?> table, String placeholderText) {
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setFixedCellSize(36);
        table.setPlaceholder(new Label(placeholderText == null ? "No data to show." : placeholderText));
    }

    public static void styleAlertLabel(Label l, boolean danger) {
        l.setFont(font(14));
        if (danger) {
            l.setStyle("-fx-text-fill: #c0392b; -fx-font-weight: 800;");
        } else {
            l.setStyle("-fx-text-fill: rgba(0,0,0,0.65); -fx-font-weight: 700;");
        }
    }

    public static Region vSpacer(double h) {
        Region r = new Region();
        r.setMinHeight(h);
        r.setPrefHeight(h);
        r.setMaxHeight(h);
        return r;
    }

    public static Region hSpacer() {
        Region r = new Region();
        HBox.setHgrow(r, Priority.ALWAYS);
        return r;
    }

    public static void styleCard(Region card) {
        card.setBackground(new Background(new BackgroundFill(
                SURFACE, new CornerRadii(RADIUS), Insets.EMPTY
        )));
        card.setBorder(new Border(new BorderStroke(
                BORDER, BorderStrokeStyle.SOLID, new CornerRadii(RADIUS), new BorderWidths(1)
        )));
        card.setPadding(CARD_PADDING);
        card.setEffect(new DropShadow(18, Color.rgb(0, 0, 0, 0.12)));
    }


    public static void styleTitle(Label l) {
        l.setFont(Font.font("System", FONT_TITLE));
        l.setTextFill(TEXT);
    }

    public static void styleSubtitle(Label l) {
        l.setFont(Font.font("System", FONT_BODY));
        l.setTextFill(MUTED_TEXT);
    }

    public static void styleSmall(Label l) {
        l.setFont(Font.font("System", FONT_SMALL));
        l.setTextFill(MUTED_TEXT);
    }

    public static void styleError(Label l) {
        l.setFont(Font.font("System", FONT_SMALL));
        l.setTextFill(DANGER);
    }

    // ===== INPUTS =====
    public static void styleTextField(TextField tf) {
        tf.setFont(Font.font("System", FONT_BODY));
        tf.setBackground(new Background(new BackgroundFill(
                Color.WHITE, new CornerRadii(RADIUS), Insets.EMPTY
        )));
        tf.setBorder(new Border(new BorderStroke(
                BORDER, BorderStrokeStyle.SOLID, new CornerRadii(RADIUS), new BorderWidths(1)
        )));
        tf.setPadding(new Insets(10, 12, 10, 12));
        tf.setMinHeight(42);
    }


    public static void applyAppBackground(Region root) {
        root.setStyle("-fx-background-color: " + COLOR_BG + ";");
    }

    public static Font font(double size) {
        Font f = loadFont(size);
        return (f != null) ? f : Font.font(Font.getDefault().getFamily(), size);
    }

    private static Font loadFont(double size) {
        try (InputStream is = BaseStyles.class.getResourceAsStream(FONT_RESOURCE)) {
            if (is == null) return null;
            return Font.loadFont(is, size);
        } catch (Exception e) {
            return null;
        }
    }


    public static ImageView buildAuthLogo(String resourcePath, double fitHeight) {
        ImageView logoView = new ImageView();

        if (resourcePath != null && !resourcePath.isBlank()) {
            InputStream is = BaseStyles.class.getResourceAsStream(resourcePath);
            if (is != null) logoView.setImage(new Image(is));
        }

        logoView.setPreserveRatio(true);
        logoView.setFitHeight(fitHeight);
        return logoView;
    }

    public static StackPane wrapWithHoverGlow(Control field) {
        StackPane wrap = new StackPane(field);
        wrap.setPadding(new Insets(2));
        wrap.setMaxWidth(499);

        DropShadow glow = new DropShadow();
        glow.setRadius(18);
        glow.setSpread(0.25);
        glow.setColor(Color.WHITE);

        wrap.setOnMouseEntered(e -> wrap.setEffect(glow));
        wrap.setOnMouseExited(e -> wrap.setEffect(null));

        return wrap;
    }

    public static void styleAuthField(TextField f) {
        f.setPrefHeight(48);
        f.setStyle("""
                -fx-background-color: rgba(255,255,255,0.50);
                -fx-background-radius: 24;
                -fx-background-insets: 0;
                
                -fx-border-color: rgba(255,255,255,1.0);
                -fx-border-width: 2;
                -fx-border-radius: 24;
                -fx-border-insets: 0;
                
                -fx-text-fill: #000000;
                -fx-prompt-text-fill: rgba(0,0,0,0.50);
                
                -fx-focus-color: transparent;
                -fx-faint-focus-color: transparent;
                
                -fx-padding: 10 14 10 14;
                """);
    }


    public static Button buildBackImageButton(String resourcePath, double size) {
        Button b = new Button();
        b.setCursor(Cursor.HAND);
        b.setFocusTraversable(false);

        b.setPrefSize(size, size);
        b.setMinSize(size, size);
        b.setMaxSize(size, size);
        b.setPadding(Insets.EMPTY);

        b.setStyle("""
                -fx-background-color: transparent;
                -fx-padding: 0;
                -fx-background-insets: 0;
                -fx-border-width: 0;
                """);

        InputStream is = BaseStyles.class.getResourceAsStream(resourcePath);
        if (is != null) {
            ImageView iv = new ImageView(new Image(is));
            iv.setPreserveRatio(true);
            iv.setFitWidth(size);
            iv.setFitHeight(size);
            b.setGraphic(iv);
        } else {
            b.setText("←");
        }

        return b;
    }


    public static void styleBackSquareButton(Button btn) {
        btn.setCursor(javafx.scene.Cursor.HAND);
        btn.setFocusTraversable(false);

        // Square
        btn.setPrefSize(44, 44);
        btn.setMinSize(44, 44);
        btn.setMaxSize(44, 44);

        // No padding so it never “shrinks” visually
        btn.setPadding(javafx.geometry.Insets.EMPTY);

        String normal = """
                -fx-background-color: white;
                -fx-background-radius: 10;
                
                -fx-border-color: rgba(0,0,0,0.18);
                -fx-border-width: 0;
                -fx-border-radius: 10;
                
                -fx-background-insets: 0;
                -fx-border-insets: 0;
                """;

        String hover = """
                -fx-background-color: #F3F4F6;   /* slight gray */
                -fx-background-radius: 10;
                
                -fx-border-color: rgba(0,0,0,0.22);
                -fx-border-width: 0;
                -fx-border-radius: 10;
                
                -fx-background-insets: 0;
                -fx-border-insets: 0;
                """;

        String pressed = """
                -fx-background-color: #E5E7EB;   /* a bit darker gray */
                -fx-background-radius: 10;
                
                -fx-border-color: rgba(0,0,0,0.24);
                -fx-border-width: 0;
                -fx-border-radius: 10;
                
                -fx-background-insets: 0;
                -fx-border-insets: 0;
                """;

        btn.setStyle(normal);

        btn.setOnMouseEntered(e -> btn.setStyle(hover));
        btn.setOnMouseExited(e -> btn.setStyle(normal));
        btn.setOnMousePressed(e -> btn.setStyle(pressed));
        btn.setOnMouseReleased(e -> btn.setStyle(btn.isHover() ? hover : normal));
    }


    public static void stylePrimaryButton(Button btn) {
        btn.setCursor(Cursor.HAND);
        btn.setFont(font(18));
        btn.setPrefHeight(48);

        btn.setStyle("""
                -fx-background-color: %s;
                -fx-text-fill: #FFFFFF;
                -fx-background-radius: 24;
                -fx-font-weight: 800;
                
                -fx-border-color: %s;
                -fx-border-width: 2;
                -fx-border-radius: 24;
                """.formatted(COLOR_PRIMARY_BTN, COLOR_PRIMARY_BTN_BORDER));

        btn.setOnMouseEntered(e -> btn.setStyle("""
                -fx-background-color: %s;
                -fx-text-fill: #FFFFFF;
                -fx-background-radius: 24;
                -fx-font-weight: 800;
                
                -fx-border-color: %s;
                -fx-border-width: 2;
                -fx-border-radius: 24;
                """.formatted(COLOR_PRIMARY_BTN_HOVER, COLOR_PRIMARY_BTN_BORDER)));

        btn.setOnMouseExited(e -> btn.setStyle("""
                -fx-background-color: %s;
                -fx-text-fill: #FFFFFF;
                -fx-background-radius: 24;
                -fx-font-weight: 800;
                
                -fx-border-color: %s;
                -fx-border-width: 2;
                -fx-border-radius: 24;
                """.formatted(COLOR_PRIMARY_BTN, COLOR_PRIMARY_BTN_BORDER)));
    }

    public static void styleGreenPrimaryButton(Button btn) {
        btn.setMaxWidth(360);
        btn.setPrefHeight(48);
        btn.setMinHeight(48);
        btn.setMaxHeight(48);

        btn.setFont(Font.font("Bahnschrift", 18));

        btn.setStyle("""
                -fx-background-color: #51b403;
                -fx-text-fill: #FFFFFF;
                -fx-background-radius: 24;
                -fx-font-weight: bold;
                -fx-font-family: "Bahnschrift";
                -fx-font-size: 19px;
                
                -fx-border-color: #1c5901;
                -fx-border-width: 2;
                -fx-border-radius: 24;
                """);

        btn.setOnMouseEntered(e -> btn.setStyle("""
                -fx-background-color: #FFFFFF;
                -fx-text-fill: #51b403;
                -fx-background-radius: 24;
                -fx-font-weight: bold;
                -fx-font-family: "Bahnschrift";
                -fx-font-size: 21px;
                
                -fx-border-color: #1c5901;
                -fx-border-width: 2;
                -fx-border-radius: 24;
                """));

        btn.setOnMouseExited(e -> btn.setStyle("""
                -fx-background-color: #51b403;
                -fx-text-fill: #FFFFFF;
                -fx-background-radius: 24;
                -fx-font-weight: bold;
                -fx-font-family: "Bahnschrift";
                -fx-font-size: 19px;
                
                -fx-border-color: #1c5901;
                -fx-border-width: 2;
                -fx-border-radius: 24;
                """));
    }

    public static void styleBigOrangeButton(Button btn) {
        btn.setCursor(Cursor.HAND);
        btn.setPrefWidth(420);
        btn.setPrefHeight(120);
        btn.setFont(font(18));

        btn.setStyle("""
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-font-weight: 800;
                -fx-background-radius: 22;
                """.formatted(COLOR_HEADER));

        btn.setEffect(new DropShadow());

        btn.setOnMouseEntered(e -> btn.setStyle("""
                -fx-background-color: #ff9f1a;
                -fx-text-fill: white;
                -fx-font-weight: 800;
                -fx-background-radius: 22;
                """));

        btn.setOnMouseExited(e -> btn.setStyle("""
                -fx-background-color: %s;
                -fx-text-fill: white;
                -fx-font-weight: 800;
                -fx-background-radius: 22;
                """.formatted(COLOR_HEADER)));
    }

    // ===== Labels =====
    public static void styleHeaderName(Label nameLabel) {
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setFont(font(19));
        nameLabel.setStyle("-fx-font-weight: 700;");
        nameLabel.setCursor(Cursor.HAND);
    }

    public static void styleCenterTitle(Label title) {
        title.setTextFill(Color.web(COLOR_HEADER));
        title.setFont(font(40));
        title.setStyle("-fx-font-weight: 800;");
    }

    public static void styleCenterSubtitle(Label sub) {
        sub.setTextFill(Color.web(COLOR_HEADER));
        sub.setFont(font(20));
        sub.setStyle("-fx-font-weight: 600;");
    }

    // =====================================================================================
    // Reusable header builder (orange bar + logo left + avatar+name right + logout menu)
    // =====================================================================================

    public static HeaderParts buildEmployeeHeader(
            String employeeFullName,
            String logoResourcePath,
            String avatarResourcePath
    ) {
        // Container
        StackPane headerBar = new StackPane();
        headerBar.setPrefHeight(60);
        headerBar.setMinHeight(60);
        headerBar.setMaxHeight(60);
        headerBar.setStyle("-fx-background-color: " + COLOR_HEADER + ";");

        HBox headerContent = new HBox(15);
        headerContent.setAlignment(Pos.CENTER_LEFT);
        headerContent.setPadding(new Insets(0, 18, 0, 18));

        // Logo
        ImageView logo = new ImageView();
        if (logoResourcePath != null && !logoResourcePath.isBlank()) {
            InputStream is = BaseStyles.class.getResourceAsStream(logoResourcePath);
            if (is != null) logo.setImage(new Image(is));
        }
        logo.setFitHeight(55);
        logo.setPreserveRatio(true);

        // Avatar (round)
        ImageView picView = new ImageView();
        StackPane picFrame = new StackPane();

        double size = 40;
        if (avatarResourcePath != null && !avatarResourcePath.isBlank()) {
            InputStream is = BaseStyles.class.getResourceAsStream(avatarResourcePath);
            if (is != null) picView.setImage(new Image(is));
        }
        picView.setFitWidth(size);
        picView.setFitHeight(size);
        picView.setPreserveRatio(false);
        picView.setClip(new Circle(size / 2, size / 2, size / 2));

        picFrame.getChildren().setAll(picView);
        picFrame.setPrefSize(size + 6, size + 6);
        picFrame.setMinSize(size + 6, size + 6);
        picFrame.setMaxSize(size + 6, size + 6);
        picFrame.setPadding(new Insets(3));
        picFrame.setStyle("""
                -fx-background-color: rgba(255,255,255,0.35);
                -fx-background-radius: 999;
                """);

        // Name (clickable)
        Label nameLabel = new Label(employeeFullName == null ? "" : employeeFullName.trim());
        styleHeaderName(nameLabel);

        // Hover underline
        nameLabel.setOnMouseEntered(e -> nameLabel.setUnderline(true));
        nameLabel.setOnMouseExited(e -> nameLabel.setUnderline(false));

        ContextMenu accountMenu = new ContextMenu();

        MenuItem changePassItem = new MenuItem("Change Password");
        MenuItem logoutItem = new MenuItem("Logout");
        accountMenu.getItems().setAll(changePassItem, logoutItem);
        String menuItemCss = """
                -fx-font-size: 19px;
                -fx-font-weight: 700;
                -fx-padding: 7 12 7 12;
                """;
        changePassItem.setStyle(menuItemCss);
        logoutItem.setStyle(menuItemCss);

        accountMenu.setStyle("""
                -fx-background-radius: 0;
                -fx-padding: 3;
                """);

        nameLabel.setOnMouseClicked(e -> {
            if (accountMenu.isShowing()) {
                accountMenu.hide();
            } else {
                double w = nameLabel.getWidth();
                String wCss = """
                        -fx-font-size: 19px;
                        -fx-font-weight: 700;
                        -fx-padding: 7 12 7 12;
                        -fx-min-width: %spx;
                        """.formatted(w + 24);

                changePassItem.setStyle(wCss);
                logoutItem.setStyle(wCss);

                accountMenu.show(nameLabel, Side.BOTTOM, 0, 6);
            }
        });

        // Spacer pushes right side to end
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        // Right side
        HBox rightBox = new HBox(10, picFrame, nameLabel);
        rightBox.setAlignment(Pos.CENTER_RIGHT);

        headerContent.getChildren().addAll(logo, spacer, rightBox);
        headerBar.getChildren().add(headerContent);

        return new HeaderParts(headerBar, headerContent, logo, picFrame, picView, nameLabel, accountMenu, changePassItem, logoutItem);
    }

    public static final class HeaderParts {
        public final StackPane headerBar;
        public final HBox headerContent;
        public final ImageView logoView;

        public final StackPane avatarFrame;
        public final ImageView avatarView;

        public final Label nameLabel;

        public final ContextMenu accountMenu;
        public final MenuItem changePasswordItem;
        public final MenuItem logoutItem;
        private HeaderParts(StackPane headerBar,
                            HBox headerContent,
                            ImageView logoView,
                            StackPane avatarFrame,
                            ImageView avatarView,
                            Label nameLabel,
                            ContextMenu accountMenu,
                            MenuItem changePasswordItem,
                            MenuItem logoutItem) {
            this.headerBar = headerBar;
            this.headerContent = headerContent;
            this.logoView = logoView;
            this.avatarFrame = avatarFrame;
            this.avatarView = avatarView;
            this.nameLabel = nameLabel;
            this.accountMenu = accountMenu;
            this.changePasswordItem = changePasswordItem;
            this.logoutItem = logoutItem;
        }

        public void setEmployeeName(String fullName) {
            nameLabel.setText(fullName == null ? "" : fullName.trim());
        }

        public void setOnLogout(Runnable action) {
            logoutItem.setOnAction(e -> {
                accountMenu.hide();
                if (action != null) action.run();
            });
        }

        public void setOnChangePassword(Runnable action) {
            changePasswordItem.setOnAction(e -> {
                accountMenu.hide();
                if (action != null) action.run();
            });
        }

        public void setAvatarImage(Image img) {
            avatarView.setImage(img);
        }

        public void setLogoImage(Image img) {
            logoView.setImage(img);
        }
    }
}