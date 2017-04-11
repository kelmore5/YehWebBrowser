import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * <pre class="doc_header">
 * <p>
 * </pre>
 *
 * @author kelmore5
 * @custom.date 3/18/17
 */
public class YehWebBrowser extends JFrame implements Runnable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private WebBrowserTabs tabbedPane;
    private JMenu tabHistory;
    private ArrayList<DoubleLinkedList<HistoryMenu>> histories;

    private YehWebBrowser() {
        super("Web Browser");
        tabHistory = new JMenu("History");
        histories = new ArrayList<>();
        tabbedPane = new WebBrowserTabs();
        tabbedPane.createBrowserPane();
        setLayout(new BorderLayout());

        this.getContentPane().add(tabbedPane, BorderLayout.CENTER);

        makeMenus();
    }

    private void makeMenus()
    {
        JMenu fileMenu = new JMenu("File");

        JMenuItem newTab = new JMenuItem("New Tab");
        newTab.addActionListener(e -> tabbedPane.createBrowserPane());
        fileMenu.add(newTab);

        JMenuItem closeTab = new JMenuItem("Close Tab");
        closeTab.addActionListener(e -> {
            if(tabbedPane.getTabCount() == 1)
                System.exit(0);
            else
                tabbedPane.closePane();
        });
        fileMenu.add(closeTab);



        fileMenu.addSeparator();

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> System.exit(0));
        fileMenu.add(exit);

        fileMenu.setMnemonic('F');

		/*final JMenu bookmarkMenu = new JMenu("Bookmarks"); //Kyle
		JMenuItem addBookmark = new JMenuItem("Add to Bookmarks");
		addBookmark.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e)
			{
				if(bookmarks.isEmpty())
				{
					bookmarks.add(tabbedPane.getCurrentURL());
					bookmarkMenu.add(new BookmarkMenuItem(tabbedPane.getCurrentURL()));
				}
				else
				{
					if(!checkBookmark(tabbedPane.getCurrentURL()))
					{
						bookmarks.add(tabbedPane.getCurrentURL());
						bookmarkMenu.add(new BookmarkMenuItem(tabbedPane.getCurrentURL()));
					}
				}
			}
		});
		bookmarkMenu.add(addBookmark);
		bookmarkMenu.addSeparator();*/

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(tabHistory);
        setJMenuBar(menuBar);
    }

    public void run() {
        setSize(500,500);
        setVisible(true);
    }

    public static void main(String[] args) {
        YehWebBrowser yb = new YehWebBrowser();
        javax.swing.SwingUtilities.invokeLater(yb);
    }

    private class WebBrowserTabs extends JTabbedPane implements ChangeListener {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private MainPanel currentBrowserPane;
        private int historyIndex;

        WebBrowserTabs() {
            super();
            addChangeListener(this);
            historyIndex = 0;
        }

        public void setTitle(GetURL gurl)
        {
            this.setTitleAt(tabbedPane.getSelectedIndex(), gurl.getTitle());
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            //currentBrowserPane = (JEditorPane)(((JScrollPane)((MainPanel)tabbedPane.getComponentAt(tabbedPane.getSelectedIndex())).getComponent(1)).getViewport().getView());
            //currentBrowserPane = (((MainPanel)tabbedPane.getSelectedComponent()).getComponent(1)));
            currentBrowserPane = (MainPanel) tabbedPane.getSelectedComponent();
        }

        void createBrowserPane()
        {
            GetURL gurl = new GetURL("http://en.wikipedia.org/wiki/Main_Page");
            MainPanel panel = new MainPanel(gurl, historyIndex++);
            currentBrowserPane = panel;
            this.addTab(gurl.getTitle(), panel);
            this.setSelectedIndex(this.getTabCount()-1);
        }

        void closePane() {
            this.removeTabAt(this.getSelectedIndex());
        }

        MainPanel getBrowserPane() {
            return currentBrowserPane;
        }

        private class MainPanel extends JPanel implements HyperlinkListener {
            /**
             *
             */
            private static final long serialVersionUID = 1L;
            private JTextField urlTextField;
            private JEditorPane browserPane;
            private DoubleLinkedList<HistoryMenu> history;
            private int index;

            MainPanel(GetURL gurl, int _index) {
                super(new BorderLayout());
                index = _index;
                history = new DoubleLinkedList<>();
                histories.add(history);
                HistoryMenu hm = new HistoryMenu(gurl, index);
                history.add(hm);
                tabHistory.add(hm);
                urlTextField = new JTextField(gurl.getURL().toString());
                browserPane = new JEditorPane();
                browserPane.setEditable(false);
                browserPane.addHyperlinkListener(this);

                this.add(new WebToolBar(), BorderLayout.NORTH);
                this.add(new JScrollPane(browserPane), BorderLayout.CENTER);

                displayPage(gurl);
            }

			/*public JEditorPane getPane() {
				return browserPane;
			}*/

            DoubleLinkedList<HistoryMenu> getHistory() {
                return history;
            }

            JTextField getTextField() {
                return urlTextField;
            }

            void displayPage(GetURL pageURL)
            {
                try {
                    browserPane.setPage(pageURL.getURL());
                }
                catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }

            private void displayPage(String errorMessage)
            {
                browserPane.setText(errorMessage);
            }

            void goToURL(GetURL gurl, boolean addToHistory) {
                displayPage(gurl);
                tabbedPane.setTitle(gurl);
                //noinspection StatementWithEmptyBody
                if(tabbedPane.getSelectedIndex() != histories.indexOf(history)) {

                }
                if(addToHistory) {
                    HistoryMenu hm = new HistoryMenu(gurl, index);
                    history.getCurrent().add(hm);
                    history.add(hm);
                }
            }

            void goToURL(GetURL gurl, Node<HistoryMenu> node)
            {
                displayPage(gurl);
                tabbedPane.setTitle(gurl);
                history.getCurrent().add(new HistoryMenu(node.getElement()));
                history.add(node);
            }

            private void goURL(GetURL gurl, boolean addToHistory)
            {
                if(gurl.getURL() == null)
                {
                    displayPage("Cannot find site: " + urlTextField.getText());
                    tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), "Error!");
                }
                else
                    goToURL(gurl, addToHistory);
            }

            public void hyperlinkUpdate(HyperlinkEvent event)
            {
                if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                {
                    GetURL gurl = new GetURL(event.getURL().toString());
                    goToURL(gurl, true);
                    urlTextField.setText(gurl.getURL().toString());
                }
            }

            private class WebToolBar extends JToolBar {
                /**
                 *
                 */
                private static final long serialVersionUID = 1L;

                WebToolBar() {

                    // navigate webBrowser to user-entered URL
                    urlTextField.addActionListener(e -> {
                        GetURL gurl = new GetURL(urlTextField.getText());
                        goURL(gurl, true);
                    });

                    JButton backButton = new JButton("back");
                    backButton.addActionListener(event -> {
                        history.back();
                        GetURL gurl = history.getCurrent().getURL();
                        goURL(gurl, false);
                        urlTextField.setText(gurl.getURL().toString());
                    });

                    JButton forwardButton = new JButton("forward");
                    forwardButton.addActionListener(e -> {
                        history.forward();
                        GetURL gurl = history.getCurrent().getURL();
                        goURL(gurl, false);
                        urlTextField.setText(gurl.getURL().toString());
                    });

                    JButton goButton = new JButton("Go");
                    goButton.addActionListener(e -> {
                        GetURL gurl = new GetURL(urlTextField.getText());
                        goURL(gurl, true);
                    });

                    add(backButton);
                    add(forwardButton);
                    add(urlTextField);
                    add(goButton);
                }
            }
        }
    }

    private class HistoryMenu extends JMenu implements MouseListener {
        /**
         *
         */
        private static final long serialVersionUID = 1L;
        private GetURL gurl;
        private int index;

        HistoryMenu(GetURL _gurl, int _index) {
            super(_gurl.getTitle());
            gurl = _gurl;
            addMouseListener(this);
            index = _index;
        }

        HistoryMenu(HistoryMenu hm) {
            super(hm.getURL().getTitle());
            index = tabbedPane.getSelectedIndex();
            gurl = hm.getURL();
            addMouseListener(this);
        }

        GetURL getURL() {
            return gurl;
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(index != tabbedPane.getSelectedIndex()) {
                tabbedPane.getBrowserPane().goToURL(gurl, histories.get(index).getCurrentNodeCopy());
            }
            else {
                tabbedPane.getBrowserPane().goURL(gurl, false);
                tabbedPane.getBrowserPane().getHistory().setCurrent(this);
            }
            tabbedPane.getBrowserPane().getTextField().setText(gurl.getURL().toString());
        }

        @Override
        public void mousePressed(MouseEvent e) {}

        @Override
        public void mouseReleased(MouseEvent e) {}

        @Override
        public void mouseEntered(MouseEvent e) {}

        @Override
        public void mouseExited(MouseEvent e) {}

    }

    @SuppressWarnings({"TypeParameterHidesVisibleType"})
    private class DoubleLinkedList<HistoryMenu> {
        private Node<HistoryMenu> root;
        private Node<HistoryMenu> current;

        DoubleLinkedList() {
            root = current = null;
        }

        public void add(HistoryMenu datum) {
            Node<HistoryMenu> temp = new Node<>(datum);
            if(root == null) {
                root = temp;
                current = root;
            }
            else {
                temp.setPrevious(current);
                current.addNext(temp);
                current = temp;
            }
        }

        public void add(Node<HistoryMenu> n) {
            n.setPrevious(current);
            current.addNext(n);
            current = n;
        }

        void back() {
            if(current.getPrevious() != null) {
                current = current.getPrevious();
            }
        }

        void forward() {
            if(!current.getNext().isEmpty()) {
                current = current.getNext().get(current.getNext().size()-1);
            }
        }

        Node<HistoryMenu> getCurrentNodeCopy() {
            return new Node<>(current);
        }

        public void setCurrent(HistoryMenu hm) {
            setHelper(root, hm);
        }

        void setHelper(Node<HistoryMenu> n, HistoryMenu hm) {
            if(n != null) {
                if (n.getElement().equals(hm)) {
                    current = n;
                } else {
                    for (Node<HistoryMenu> nn : n.getNext()) {
                        setHelper(nn, hm);
                    }
                }
            }
        }


        public HistoryMenu getCurrent() {
            return current.getElement();
        }
    }

    @SuppressWarnings({"TypeParameterHidesVisibleType"})
    private class Node<HistoryMenu>
    {
        private ArrayList<Node<HistoryMenu>> next;
        private Node<HistoryMenu> previous;
        private HistoryMenu object;

        Node(HistoryMenu element)
        {
            object = element;
            next = new ArrayList<>();
            previous = null;
        }

        Node(Node<HistoryMenu> n) {
            next = new ArrayList<>(n.getNext());
            object = n.getElement();
            previous = null;
        }

        Node<HistoryMenu> getPrevious() {
            return previous;
        }

        void setPrevious(Node<HistoryMenu> _previous) {
            previous = _previous;
        }

        public HistoryMenu getElement() {
            return object;
        }

        public ArrayList<Node<HistoryMenu>> getNext() {
            return next;
        }

        void addNext(Node<HistoryMenu> _next) {
            next.add(_next);
        }
    }
}