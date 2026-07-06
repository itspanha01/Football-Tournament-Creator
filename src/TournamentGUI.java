import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class TournamentGUI extends JFrame {

    private ArrayList<String> teamNames  = new ArrayList<>();
    private ArrayList<Color>  teamColors = new ArrayList<>();

    private CardLayout cardLayout;
    private JPanel     mainPanel;

    // ── Theme ────────────────────────────────────────────────────────────────
    static final Color BG      = new Color(15,  15,  20);
    static final Color PANEL   = new Color(25,  25,  35);
    static final Color CARD    = new Color(38,  38,  55);
    static final Color ACCENT  = new Color(50, 205, 130);
    static final Color TEXT    = Color.WHITE;
    static final Color MUTED   = new Color(140, 140, 160);
    static final Color BORDER  = new Color(55,  55,  75);
    static final Color RED_ERR = new Color(255,  80,  80);

    // ── Font loading ─────────────────────────────────────────────────────────
    static final Font GEIST_BASE;
    static {
        Font f = null;
        try {
            f = Font.createFont(Font.TRUETYPE_FONT,
                new File("C:/Users/Sopanha/Downloads/Pixelify_Sans/PixelifySans-VariableFont_wght.ttf"));
            GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(f);
        } catch (FontFormatException | IOException e) {
            System.out.println("[Info] Pixelify Sans not found — using Segoe UI fallback.");
        }
        GEIST_BASE = f != null ? f : new Font("Segoe UI", Font.PLAIN, 12);
    }

    static final Font TITLE_F  = GEIST_BASE.deriveFont(Font.PLAIN, 28f);
    static final Font HEAD_F   = GEIST_BASE.deriveFont(Font.PLAIN, 16f);
    static final Font BODY_F   = GEIST_BASE.deriveFont(Font.PLAIN, 14f);
    static final Font MONO_F   = GEIST_BASE.deriveFont(Font.PLAIN, 13f);

    // ── Entry point ──────────────────────────────────────────────────────────
    public TournamentGUI() {
        setTitle("Football Tournament Creator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 650);
        setMinimumSize(new Dimension(750, 500));
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel  = new JPanel(cardLayout);
        mainPanel.setBackground(BG);

        mainPanel.add(buildMenuPanel(),        "MENU");
        mainPanel.add(buildCreateTeamsPanel(), "CREATE");
        mainPanel.add(placeholder("SHOW"),     "SHOW");
        mainPanel.add(placeholder("EDIT"),     "EDIT");
        mainPanel.add(placeholder("BRACKET"),  "BRACKET");

        add(mainPanel);
    }

    private JPanel placeholder(String name) {
        JPanel p = new JPanel();
        p.setName(name);
        p.setBackground(BG);
        return p;
    }

    // ── Refresh dynamic panels ───────────────────────────────────────────────
    private void refreshAndShow(String name) {
        for (Component c : mainPanel.getComponents()) {
            if (name.equals(c.getName())) { mainPanel.remove(c); break; }
        }
        JPanel fresh = switch (name) {
            case "SHOW"    -> buildShowTeamsPanel();
            case "EDIT"    -> buildEditTeamsPanel();
            case "BRACKET" -> buildBracketPanel();
            default        -> placeholder(name);
        };
        mainPanel.add(fresh, name);
        mainPanel.revalidate();
        mainPanel.repaint();
        cardLayout.show(mainPanel, name);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private JButton styledButton(String text) {
        JButton b = new JButton(text);
        b.setFont(HEAD_F);
        b.setForeground(TEXT);
        b.setBackground(CARD);
        b.setOpaque(true);
        b.setBorderPainted(true);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(12, 30, 12, 30)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(new Color(60, 60, 85)); }
            public void mouseExited (MouseEvent e) { b.setBackground(CARD); }
        });
        return b;
    }

    private JButton menuButton(String text) {
        Color DARK_BLUE       = new Color(15, 35, 90);
        Color DARK_BLUE_HOVER = new Color(25, 55, 130);
        JButton b = new JButton(text);
        b.setFont(HEAD_F);
        b.setForeground(TEXT);
        b.setBackground(DARK_BLUE);
        b.setOpaque(true);
        b.setBorderPainted(true);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BORDER, 1, true),
            BorderFactory.createEmptyBorder(12, 30, 12, 30)));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(DARK_BLUE_HOVER); }
            public void mouseExited (MouseEvent e) { b.setBackground(DARK_BLUE); }
        });
        return b;
    }

    private JButton accentButton(String text) {
        JButton b = styledButton(text);
        b.setBackground(ACCENT);
        b.setForeground(Color.BLACK);
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(ACCENT.darker()); }
            public void mouseExited (MouseEvent e) { b.setBackground(ACCENT); }
        });
        return b;
    }

    private JLabel lbl(String t, Font f, Color c) {
        JLabel l = new JLabel(t); l.setFont(f); l.setForeground(c); return l;
    }

    private JPanel buildHeader(String title, String sub) {
        JPanel h = new JPanel();
        h.setBackground(PANEL);
        h.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER),
            BorderFactory.createEmptyBorder(20, 30, 20, 30)));
        h.setLayout(new BoxLayout(h, BoxLayout.Y_AXIS));
        JLabel t = lbl(title, TITLE_F, TEXT); t.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel s = lbl(sub,   BODY_F,  MUTED); s.setAlignmentX(Component.LEFT_ALIGNMENT);
        h.add(t); h.add(Box.createVerticalStrut(4)); h.add(s);
        return h;
    }

    private Color colorFromName(String name) {
        return switch (name) {
            case "Red"    -> new Color(220,  50,  50);
            case "Yellow" -> new Color(230, 200,   0);
            case "Green"  -> new Color( 50, 200,  80);
            case "Blue"   -> new Color( 50, 100, 220);
            case "Purple" -> new Color(150,  50, 200);
            default       -> TEXT;
        };
    }

    private String nameForColor(Color c) {
        if (c.equals(new Color(220, 50,  50)))  return "Red";
        if (c.equals(new Color(230, 200,  0)))  return "Yellow";
        if (c.equals(new Color( 50, 200, 80)))  return "Green";
        if (c.equals(new Color( 50, 100, 220))) return "Blue";
        if (c.equals(new Color(150,  50, 200))) return "Purple";
        return "Default";
    }

    // ── MENU ─────────────────────────────────────────────────────────────────
    private JPanel buildMenuPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setName("MENU");

        JPanel titleArea = new JPanel();
        titleArea.setBackground(BG);
        titleArea.setLayout(new BoxLayout(titleArea, BoxLayout.Y_AXIS));
        titleArea.setBorder(BorderFactory.createEmptyBorder(50, 0, 20, 0));

        JLabel trophy   = lbl("🏆", new Font("Segoe UI Emoji", Font.PLAIN, 48), TEXT);
        JLabel title    = lbl("Tournament Creator", TITLE_F, TEXT);
        JLabel subtitle = lbl("Football Edition",   BODY_F,  ACCENT);
        for (JLabel l : new JLabel[]{trophy, title, subtitle})
            l.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleArea.add(trophy);
        titleArea.add(Box.createVerticalStrut(10));
        titleArea.add(title);
        titleArea.add(Box.createVerticalStrut(5));
        titleArea.add(subtitle);

        JPanel btnArea = new JPanel(new GridLayout(5, 1, 0, 10));
        btnArea.setBackground(BG);
        btnArea.setBorder(BorderFactory.createEmptyBorder(20, 200, 60, 200));

        JButton showBracket = menuButton("⚽  Show Bracket");
        JButton createTeams = menuButton("＋  Create Teams");
        JButton editTeams   = menuButton("✎   Edit Teams");
        JButton showTeams   = menuButton("☰   Show Teams");
        JButton exit        = menuButton("✕   Exit");
        exit.setForeground(RED_ERR);

        showBracket.addActionListener(e -> refreshAndShow("BRACKET"));
        createTeams.addActionListener(e -> cardLayout.show(mainPanel, "CREATE"));
        editTeams  .addActionListener(e -> refreshAndShow("EDIT"));
        showTeams  .addActionListener(e -> refreshAndShow("SHOW"));
        exit       .addActionListener(e -> System.exit(0));

        btnArea.add(showBracket);
        btnArea.add(createTeams);
        btnArea.add(editTeams);
        btnArea.add(showTeams);
        btnArea.add(exit);

        p.add(titleArea, BorderLayout.NORTH);
        p.add(btnArea,   BorderLayout.CENTER);
        return p;
    }

    // ── CREATE TEAMS ─────────────────────────────────────────────────────────
    private JPanel buildCreateTeamsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setName("CREATE");
        p.add(buildHeader("Create Teams", "Pick a team count, enter 3-letter abbreviations"), BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setBackground(BG);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(20, 80, 10, 80));

        // Count selector
        JLabel countLbl = lbl("Number of Teams:", HEAD_F, TEXT);
        countLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel countRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        countRow.setBackground(BG);
        countRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        int[]      counts     = {2, 4, 8, 16};
        int[]      selected   = {4};
        JButton[]  countBtns  = new JButton[4];

        for (int i = 0; i < counts.length; i++) {
            final int cnt = counts[i];
            JButton cb = new JButton(String.valueOf(cnt));
            cb.setFont(HEAD_F);
            cb.setForeground(cnt == 4 ? Color.BLACK : TEXT);
            cb.setBackground(cnt == 4 ? ACCENT : CARD);
            cb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(8, 20, 8, 20)));
            cb.setFocusPainted(false);
            cb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            countBtns[i] = cb;
            countRow.add(cb);
        }

        // Team entry rows (max 16)
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setBackground(PANEL);
        fieldsPanel.setLayout(new BoxLayout(fieldsPanel, BoxLayout.Y_AXIS));
        fieldsPanel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        JTextField[]   nameFields = new JTextField[16];
        JComboBox<?>[] colorBoxes = new JComboBox[16];
        JPanel[]       rows       = new JPanel[16];
        String[]       colorOpts  = {"Default","Red","Yellow","Green","Blue","Purple"};

        for (int i = 0; i < 16; i++) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
            row.setBackground(PANEL);

            JLabel num = lbl(String.format("Team %2d:", i + 1), MONO_F, MUTED);
            num.setPreferredSize(new Dimension(75, 25));

            JTextField tf = new JTextField(6);
            tf.setFont(MONO_F);
            tf.setBackground(CARD);
            tf.setForeground(TEXT);
            tf.setCaretColor(TEXT);
            tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));

            JComboBox<String> cb = new JComboBox<>(colorOpts);
            cb.setFont(BODY_F);
            cb.setBackground(CARD);

            nameFields[i] = tf;
            colorBoxes[i] = cb;
            row.add(num); row.add(tf); row.add(cb);
            rows[i] = row;
        }

        // Rebuild visible rows when count is chosen
        Runnable rebuildFields = () -> {
            fieldsPanel.removeAll();
            for (int i = 0; i < selected[0]; i++) fieldsPanel.add(rows[i]);
            fieldsPanel.revalidate();
            fieldsPanel.repaint();
        };

        for (int i = 0; i < counts.length; i++) {
            final int cnt = counts[i];
            final int bi  = i;
            countBtns[i].addActionListener(e -> {
                selected[0] = cnt;
                for (int j = 0; j < countBtns.length; j++) {
                    countBtns[j].setBackground(CARD);
                    countBtns[j].setForeground(TEXT);
                }
                countBtns[bi].setBackground(ACCENT);
                countBtns[bi].setForeground(Color.BLACK);
                rebuildFields.run();
            });
        }
        rebuildFields.run(); // show default 4

        JScrollPane scroll = new JScrollPane(fieldsPanel);
        scroll.setBackground(PANEL);
        scroll.setBorder(BorderFactory.createLineBorder(BORDER));
        scroll.setPreferredSize(new Dimension(500, 260));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel status = lbl("", BODY_F, RED_ERR);
        status.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel teamLbl = lbl("Team Abbreviations (exactly 3 letters):", HEAD_F, TEXT);
        teamLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        center.add(countLbl);
        center.add(Box.createVerticalStrut(8));
        center.add(countRow);
        center.add(Box.createVerticalStrut(20));
        center.add(teamLbl);
        center.add(Box.createVerticalStrut(8));
        center.add(scroll);
        center.add(Box.createVerticalStrut(8));
        center.add(status);

        p.add(center, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        footer.setBackground(BG);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 60));

        JButton back = styledButton("Back");
        JButton save = accentButton("Save Teams");

        back.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        save.addActionListener(e -> {
            int count = selected[0];
            ArrayList<String> newNames  = new ArrayList<>();
            ArrayList<Color>  newColors = new ArrayList<>();

            for (int i = 0; i < count; i++) {
                String name = nameFields[i].getText().strip().toUpperCase();
                if (name.isEmpty()) {
                    status.setForeground(RED_ERR);
                    status.setText("Team " + (i + 1) + " name is empty!");
                    return;
                }
                if (name.length() != 3) {
                    status.setForeground(RED_ERR);
                    status.setText("Team " + (i + 1) + ": must be exactly 3 letters!");
                    return;
                }
                if (newNames.contains(name)) {
                    status.setForeground(RED_ERR);
                    status.setText("Duplicate: " + name);
                    return;
                }
                newNames.add(name);
                newColors.add(colorFromName((String) colorBoxes[i].getSelectedItem()));
            }

            teamNames  = newNames;
            teamColors = newColors;
            status.setForeground(ACCENT);
            status.setText("✓ " + count + " teams saved!");

            // Clear fields for next session
            for (int i = 0; i < 16; i++) {
                nameFields[i].setText("");
                colorBoxes[i].setSelectedIndex(0);
            }
        });

        footer.add(back);
        footer.add(save);
        p.add(footer, BorderLayout.SOUTH);
        return p;
    }

    // ── SHOW TEAMS ────────────────────────────────────────────────────────────
    private JPanel buildShowTeamsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setName("SHOW");
        p.add(buildHeader("Show Teams", teamNames.size() + " teams registered"), BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setBackground(BG);
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));

        if (teamNames.isEmpty()) {
            JLabel empty = lbl("No teams yet. Use Create Teams first.", HEAD_F, MUTED);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            center.add(Box.createVerticalGlue());
            center.add(empty);
            center.add(Box.createVerticalGlue());
        } else {
            int cols = teamNames.size() == 16 ? 2 : 1;
            JPanel grid = new JPanel(new GridLayout(0, cols, 15, 8));
            grid.setBackground(BG);
            grid.setAlignmentX(Component.LEFT_ALIGNMENT);

            for (int i = 0; i < teamNames.size(); i++) {
                JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
                card.setBackground(CARD);
                card.setBorder(BorderFactory.createLineBorder(BORDER, 1, true));
                card.add(lbl(String.format("%2d.", i + 1), MONO_F, MUTED));
                card.add(lbl(teamNames.get(i), HEAD_F, teamColors.get(i)));
                grid.add(card);
            }
            center.add(grid);
        }

        JScrollPane scroll = new JScrollPane(center);
        scroll.setBackground(BG);
        scroll.setBorder(null);
        p.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        footer.setBackground(BG);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 60, 10, 0));
        JButton back = styledButton("Back to Menu");
        back.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        footer.add(back);
        p.add(footer, BorderLayout.SOUTH);
        return p;
    }

    // ── EDIT TEAMS ────────────────────────────────────────────────────────────
    private JPanel buildEditTeamsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setName("EDIT");
        p.add(buildHeader("Edit Teams", "Select a team from the list to modify or delete"), BorderLayout.NORTH);

        if (teamNames.isEmpty()) {
            JPanel center = new JPanel(new BorderLayout());
            center.setBackground(BG);
            JLabel empty = lbl("No teams to edit. Create teams first.", HEAD_F, MUTED);
            empty.setHorizontalAlignment(SwingConstants.CENTER);
            center.add(empty, BorderLayout.CENTER);
            p.add(center, BorderLayout.CENTER);
        } else {
            JPanel split = new JPanel(new GridLayout(1, 2, 15, 0));
            split.setBackground(BG);
            split.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

            // ── Left: team list ──────────────────────────────────────────────
            JPanel listPanel = new JPanel();
            listPanel.setBackground(PANEL);
            listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
            listPanel.setBorder(BorderFactory.createLineBorder(BORDER));

            JLabel listTitle = lbl(" Teams", HEAD_F, TEXT);
            listTitle.setOpaque(true);
            listTitle.setBackground(CARD);
            listTitle.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
            listPanel.add(listTitle);

            // shared edit state
            int[]      selectedIdx = {0};
            JTextField editName    = new JTextField(10);
            editName.setFont(MONO_F);
            editName.setBackground(CARD);
            editName.setForeground(TEXT);
            editName.setCaretColor(TEXT);
            editName.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
            editName.setMaximumSize(new Dimension(200, 40));
            editName.setAlignmentX(Component.LEFT_ALIGNMENT);

            String[]       colorOpts = {"Default","Red","Yellow","Green","Blue","Purple"};
            JComboBox<String> colorBox = new JComboBox<>(colorOpts);
            colorBox.setFont(BODY_F);
            colorBox.setBackground(CARD);
            colorBox.setMaximumSize(new Dimension(200, 40));
            colorBox.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel editStatus = lbl("", BODY_F, ACCENT);
            editStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

            // populate initial form values
            editName.setText(teamNames.get(0));
            colorBox.setSelectedItem(nameForColor(teamColors.get(0)));

            ButtonGroup bg = new ButtonGroup();
            for (int i = 0; i < teamNames.size(); i++) {
                final int idx = i;
                JToggleButton tb = new JToggleButton(
                    String.format(" %d.  %s", i + 1, teamNames.get(i)));
                tb.setFont(MONO_F);
                tb.setForeground(teamColors.get(i));
                tb.setBackground(PANEL);
                tb.setHorizontalAlignment(SwingConstants.LEFT);
                tb.setBorderPainted(false);
                tb.setFocusPainted(false);
                tb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                tb.addActionListener(e -> {
                    selectedIdx[0] = idx;
                    editName.setText(teamNames.get(idx));
                    colorBox.setSelectedItem(nameForColor(teamColors.get(idx)));
                    editStatus.setText("");
                });
                if (i == 0) tb.setSelected(true);
                bg.add(tb);
                listPanel.add(tb);
            }
            listPanel.add(Box.createVerticalGlue());
            JScrollPane listScroll = new JScrollPane(listPanel);
            listScroll.setBorder(null);

            // ── Right: form ──────────────────────────────────────────────────
            JPanel form = new JPanel();
            form.setBackground(PANEL);
            form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
            form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

            JLabel formTitle = lbl("Edit Selected Team", HEAD_F, TEXT);
            formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

            JLabel nameLbl  = lbl("Team Name (3 letters):", BODY_F, MUTED);
            nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel colorLbl = lbl("Team Color:", BODY_F, MUTED);
            colorLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

            JButton saveBtn   = accentButton("Save Changes");
            JButton deleteBtn = styledButton("Delete Team");
            saveBtn  .setAlignmentX(Component.LEFT_ALIGNMENT);
            deleteBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
            deleteBtn.setForeground(RED_ERR);

            saveBtn.addActionListener(e -> {
                int idx     = selectedIdx[0];
                String name = editName.getText().strip().toUpperCase();
                if (name.isEmpty())     { editStatus.setForeground(RED_ERR); editStatus.setText("Name can't be empty!"); return; }
                if (name.length() != 3) { editStatus.setForeground(RED_ERR); editStatus.setText("Must be exactly 3 letters!"); return; }
                for (int i = 0; i < teamNames.size(); i++) {
                    if (i != idx && teamNames.get(i).equals(name)) {
                        editStatus.setForeground(RED_ERR); editStatus.setText("Name already exists!"); return;
                    }
                }
                teamNames .set(idx, name);
                teamColors.set(idx, colorFromName((String) colorBox.getSelectedItem()));
                editStatus.setForeground(ACCENT);
                editStatus.setText("✓ Team updated!");
                refreshAndShow("EDIT");
            });

            deleteBtn.addActionListener(e -> {
                int idx = selectedIdx[0];
                int ok  = JOptionPane.showConfirmDialog(p,
                    "Delete \"" + teamNames.get(idx) + "\"?", "Confirm", JOptionPane.YES_NO_OPTION);
                if (ok == JOptionPane.YES_OPTION) {
                    teamNames .remove(idx);
                    teamColors.remove(idx);
                    refreshAndShow("EDIT");
                }
            });

            form.add(formTitle);
            form.add(Box.createVerticalStrut(20));
            form.add(nameLbl);
            form.add(Box.createVerticalStrut(6));
            form.add(editName);
            form.add(Box.createVerticalStrut(15));
            form.add(colorLbl);
            form.add(Box.createVerticalStrut(6));
            form.add(colorBox);
            form.add(Box.createVerticalStrut(20));
            form.add(saveBtn);
            form.add(Box.createVerticalStrut(10));
            form.add(deleteBtn);
            form.add(Box.createVerticalStrut(15));
            form.add(editStatus);
            form.add(Box.createVerticalGlue());

            split.add(listScroll);
            split.add(form);
            p.add(split, BorderLayout.CENTER);
        }

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        footer.setBackground(BG);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 30, 10, 0));
        JButton back = styledButton("Back to Menu");
        back.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        footer.add(back);
        p.add(footer, BorderLayout.SOUTH);
        return p;
    }

    // ── BRACKET ───────────────────────────────────────────────────────────────
    private JPanel buildBracketPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(BG);
        p.setName("BRACKET");
        p.add(buildHeader("Tournament Bracket", "World Cup Edition"), BorderLayout.NORTH);

        JPanel canvas = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawBracket((Graphics2D) g, getWidth(), getHeight());
            }
        };
        canvas.setBackground(BG);
        canvas.setPreferredSize(new Dimension(860, 500));

        JScrollPane scroll = new JScrollPane(canvas);
        scroll.setBorder(null);
        scroll.setBackground(BG);
        p.add(scroll, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        footer.setBackground(BG);
        footer.setBorder(BorderFactory.createEmptyBorder(0, 30, 10, 0));
        JButton back = styledButton("Back to Menu");
        back.addActionListener(e -> cardLayout.show(mainPanel, "MENU"));
        footer.add(back);
        p.add(footer, BorderLayout.SOUTH);
        return p;
    }

    // ── Drawing ───────────────────────────────────────────────────────────────
    private void drawBracket(Graphics2D g, int w, int h) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,    RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int n = teamNames.size();
        if (n == 0) {
            g.setColor(MUTED); g.setFont(HEAD_F);
            String msg = "No teams yet — go to Create Teams first.";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
            return;
        }
        if      (n == 2)  draw2(g, w, h);
        else if (n == 4)  draw4(g, w, h);
        else if (n == 8)  draw8(g, w, h);
        else if (n == 16) draw16(g, w, h);
        else {
            g.setColor(MUTED); g.setFont(HEAD_F);
            String msg = "Bracket for " + n + " teams not yet supported.";
            FontMetrics fm = g.getFontMetrics();
            g.drawString(msg, (w - fm.stringWidth(msg)) / 2, h / 2);
        }
    }

    private void teamBox(Graphics2D g, int x, int y, int bw, int bh, String name, Color color) {
        g.setColor(CARD); g.fillRoundRect(x, y, bw, bh, 8, 8);
        g.setColor(BORDER); g.drawRoundRect(x, y, bw, bh, 8, 8);
        g.setFont(new Font("Consolas", Font.BOLD, 14));
        g.setColor(color);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(name, x + (bw - fm.stringWidth(name)) / 2,
                           y + (bh + fm.getAscent() - fm.getDescent()) / 2);
    }

    private void vsBox(Graphics2D g, int x, int y, int bw, int bh) {
        g.setColor(new Color(50, 50, 70)); g.fillRoundRect(x, y, bw, bh, 8, 8);
        g.setColor(ACCENT); g.drawRoundRect(x, y, bw, bh, 8, 8);
        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
        FontMetrics fm = g.getFontMetrics();
        g.setColor(ACCENT);
        g.drawString("vs", x + (bw - fm.stringWidth("vs")) / 2,
                           y + (bh + fm.getAscent()) / 2 - 2);
    }

    private void line(Graphics2D g, int x1, int y1, int x2, int y2) {
        g.setColor(BORDER); g.setStroke(new BasicStroke(2));
        g.drawLine(x1, y1, x2, y2);
    }

    private void tag(Graphics2D g, String text, int cx, int y, Color color) {
        g.setFont(new Font("Segoe UI", Font.BOLD, 11)); g.setColor(color);
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text, cx - fm.stringWidth(text) / 2, y);
    }

    // 2 teams
    private void draw2(Graphics2D g, int w, int h) {
        int bw = 80, bh = 35, cx = w / 2, cy = h / 2;
        tag(g, "★  FINAL  ★", cx, cy - 55, ACCENT);
        teamBox(g, cx - bw - 20, cy - bh/2, bw, bh, teamNames.get(0), teamColors.get(0));
        vsBox  (g, cx - 15,      cy - bh/2, 30, bh);
        teamBox(g, cx + 20,      cy - bh/2, bw, bh, teamNames.get(1), teamColors.get(1));
    }

    // 4 teams
    private void draw4(Graphics2D g, int w, int h) {
        int bw = 75, bh = 32, cx = w / 2;
        int finY = 80, midY = 200, gap = 130;

        tag(g, "★  FINAL  ★", cx, finY - 15, ACCENT);
        vsBox(g, cx - 25, finY, 50, bh);

        int[] semiX = {cx - gap, cx + gap};
        for (int s = 0; s < 2; s++) {
            int sx = semiX[s];
            tag(g, "Semi-Final", sx, midY - 12, MUTED);
            teamBox(g, sx - bw/2, midY,          bw, bh, teamNames.get(s * 2),     teamColors.get(s * 2));
            teamBox(g, sx - bw/2, midY + bh + 10, bw, bh, teamNames.get(s * 2 + 1), teamColors.get(s * 2 + 1));
            // vertical to midpoint
            line(g, sx, midY, sx, finY + bh/2);
            // horizontal to final box
            int dir = s == 0 ? -1 : 1;
            line(g, sx, finY + bh/2, cx + dir * 25, finY + bh/2);
        }
    }

    // 8 teams
    private void draw8(Graphics2D g, int w, int h) {
        int bw = 65, bh = 28, cx = w / 2;
        int finY = 60, semiY = 170, qtrY = 300;
        int semiGap = 140, qtrGap = 75;

        tag(g, "★  FINAL  ★", cx, finY - 12, ACCENT);
        vsBox(g, cx - 25, finY, 50, bh);

        int[] semiX = {cx - semiGap, cx + semiGap};
        for (int s = 0; s < 2; s++) {
            int sx = semiX[s];
            tag(g, "Semi-Final", sx, semiY - 12, MUTED);
            vsBox(g, sx - 25, semiY, 50, bh);

            int dir = s == 0 ? -1 : 1;
            line(g, sx, semiY + bh/2, sx, finY + bh/2);
            line(g, sx, finY + bh/2, cx + dir * 25, finY + bh/2);

            int[] qx = {sx - qtrGap, sx + qtrGap};
            for (int q = 0; q < 2; q++) {
                int tx = qx[q];
                int t1 = s * 4 + q * 2;
                tag(g, "QF", tx, qtrY - 10, MUTED);
                teamBox(g, tx - bw/2, qtrY,          bw, bh, teamNames.get(t1),     teamColors.get(t1));
                teamBox(g, tx - bw/2, qtrY + bh + 8, bw, bh, teamNames.get(t1 + 1), teamColors.get(t1 + 1));

                line(g, tx, qtrY, tx, semiY + bh/2);
                int qdir = q == 0 ? -1 : 1;
                line(g, tx, semiY + bh/2, sx + qdir * 25, semiY + bh/2);
            }
        }
    }

    // 16 teams — Round of 16 draw
    private void draw16(Graphics2D g, int w, int h) {
        int bw = 55, bh = 24;
        int startY = 80;

        tag(g, "Round of 16 — Draw", w / 2, 30, ACCENT);
        g.setColor(MUTED); g.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        String note = "(Full bracket progresses as results are entered)";
        FontMetrics fm = g.getFontMetrics();
        g.drawString(note, (w - fm.stringWidth(note)) / 2, 50);

        int colW  = w / 2 - 20;
        int rowH  = bh * 2 + 30;

        for (int i = 0; i < 8; i++) {
            int col = i / 4, row = i % 4;
            int mx = 30 + col * colW;
            int my = startY + row * rowH;
            int t1 = i * 2, t2 = i * 2 + 1;

            g.setColor(MUTED); g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            g.drawString("Match " + (i + 1), mx, my - 5);
            teamBox(g, mx,              my, bw, bh, teamNames.get(t1), teamColors.get(t1));
            vsBox  (g, mx + bw + 5,    my, 30, bh);
            teamBox(g, mx + bw + 40,   my, bw, bh, teamNames.get(t2), teamColors.get(t2));
        }
    }

    // ── Launch ────────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Metal L&F respects setBackground() on buttons; system L&F (Windows) does not
                UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
            } catch (Exception ignored) {}
            new TournamentGUI().setVisible(true);
        });
    }
}
