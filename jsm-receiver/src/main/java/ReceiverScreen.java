import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ReceiverScreen {
  private JTextField messageTextField;
  private JButton sendButton;
  private JList requestsList;
  private JPanel mainPanel;

  private Receiver receiver = new Receiver(new Runnable() {
    @Override
    public void run() {
      updateRequestsList(receiver.getMessagesTitles());
    }
  });

  private void updateRequestsList(List messages) {
    DefaultListModel listModel = new DefaultListModel();
    listModel.addAll(messages);
    requestsList.setModel(listModel);
    requestsList.updateUI();
    requestsList.repaint();
    requestsList.revalidate();
    if (mainPanel != null) {
      mainPanel.repaint();
      mainPanel.revalidate();
      mainPanel.updateUI();
    }
  }

  public ReceiverScreen() {
    $$$setupUI$$$();
    sendButton.addActionListener(actionEvent -> {
      if (requestsList.getSelectedIndex() != -1) {
        receiver.replyMessage(requestsList.getSelectedIndex(), messageTextField.getText());
      }
    });
    requestsList.addListSelectionListener(new ListSelectionListener() {
      @Override
      public void valueChanged(ListSelectionEvent listSelectionEvent) {
        if (listSelectionEvent.getValueIsAdjusting() == false) {

          if (requestsList.getSelectedIndex() == -1) {
            //No selection, disable fire button.
            sendButton.setEnabled(false);
            messageTextField.setEnabled(false);
          } else {
            //Selection, enable the fire button.
            sendButton.setEnabled(true);
            messageTextField.setEnabled(true);
          }
        }
      }
    });
  }

  public static void main(String[] args) {
    JFrame frame = new JFrame("Flights Booking Agency");
    try {
      frame.setIconImage(ImageIO.read(new File("img/faq.png")));
    } catch (IOException e) {
    }
    frame.setContentPane(new ReceiverScreen().mainPanel);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.pack();
    frame.setVisible(true);
  }

  private void createUIComponents() {
    requestsList = new JList();
    requestsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    updateRequestsList(receiver.getMessagesTitles());
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer
   * >>> IMPORTANT!! <<<
   * DO NOT edit this method OR call it in your code!
   *
   * @noinspection ALL
   */
  private void $$$setupUI$$$() {
    createUIComponents();
    mainPanel = new JPanel();
    mainPanel.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(3, 1, new Insets(8, 8, 8, 8), -1, -1));
    final JLabel label1 = new JLabel();
    label1.setText("Requests");
    mainPanel.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JPanel panel1 = new JPanel();
    panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
    mainPanel.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    messageTextField = new JTextField();
    messageTextField.setEnabled(false);
    messageTextField.setText("");
    messageTextField.setToolTipText("Type a message");
    panel1.add(messageTextField, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, -1), null, 0, false));
    sendButton = new JButton();
    sendButton.setEnabled(false);
    sendButton.setText("Send");
    panel1.add(sendButton, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    final JScrollPane scrollPane1 = new JScrollPane();
    mainPanel.add(scrollPane1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    scrollPane1.setViewportView(requestsList);
  }

  /**
   * @noinspection ALL
   */
  public JComponent $$$getRootComponent$$$() {
    return mainPanel;
  }

}
