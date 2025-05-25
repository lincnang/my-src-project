package com.lineage.managerUI;

import com.lineage.server.datatables.ItemTable;
import com.lineage.server.model.Instance.L1ItemInstance;
import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Inventory;
import com.lineage.server.serverpackets.S_SystemMessage;
import com.lineage.server.templates.L1Item;
import com.lineage.server.world.World;

import javax.swing.*;
import java.awt.event.MouseEvent;

@SuppressWarnings("serial")
public class Goitem extends javax.swing.JFrame {
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button buttongoitem;
    private java.awt.Label label1;
    private java.awt.Label label2;
    private java.awt.Label label3;
    private java.awt.Label label4;
    private java.awt.TextField 送禮暱稱;
    private java.awt.TextField 道具數量;
    private java.awt.TextField 道具編號;
    private java.awt.TextField 強化等級;

    /**
     * Creates new form goitem
     */
    public Goitem() {
        initComponents();
        setLocation(200, 100);
        setVisible(true);
    }

    @SuppressWarnings("unused")
    private static Goitem get() {
        // TODO Auto-generated method stub
        return null;
    }

    private void initComponents() {
        label1 = new java.awt.Label();
        送禮暱稱 = new java.awt.TextField();
        label2 = new java.awt.Label();
        道具編號 = new java.awt.TextField();
        label3 = new java.awt.Label();
        強化等級 = new java.awt.TextField();
        label4 = new java.awt.Label();
        道具數量 = new java.awt.TextField();
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("贈送禮物");
        label1.setText("暱稱");
        送禮暱稱.setText("");
        label2.setText("道具編號");
        道具編號.setText("");
        label3.setText("強化等級");
        強化等級.setText("");
        label4.setText("道具數量");
        道具數量.setText("");
        buttongoitem = new java.awt.Button();
        buttongoitem.setLabel("贈送禮物");
        buttongoitem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                buttongoitemClicked(evt);
            }
        });
        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false).addComponent(送禮暱稱, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE).addComponent(強化等級, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE)).addGap(22, 22, 22).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(道具編號, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE).addComponent(道具數量, javax.swing.GroupLayout.DEFAULT_SIZE, 63, Short.MAX_VALUE))).addGroup(layout.createSequentialGroup().addGap(117, 117, 117).addComponent(buttongoitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))).addContainerGap()));
        layout.setVerticalGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(label1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(label2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(送禮暱稱, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(道具編號, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(label3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(label4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(強化等級, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(道具數量, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addGap(19, 19, 19).addComponent(buttongoitem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        pack();
    }

    private void buttongoitemClicked(MouseEvent evt) {
        String name2 = 送禮暱稱.getText();
        int itemid = Integer.parseInt(道具編號.getText());
        int enchant = Integer.parseInt(強化等級.getText());
        int count = Integer.parseInt(道具數量.getText());
        L1Item temp = ItemTable.get().getTemplate(itemid);
        for (L1PcInstance pc : World.get().getAllPlayers()) {
            if (pc.getName().equalsIgnoreCase(name2)) {
                if (temp != null) {
                    L1ItemInstance item = ItemTable.get().createItem(itemid);
                    item.setEnchantLevel(enchant);
                    item.setCount(count);
                    int createCount;
                    if (pc.getInventory().checkAddItem(item, count) == L1Inventory.OK) {
                        pc.getInventory().storeItem(item);
                        pc.sendPackets(new S_SystemMessage(temp.getName() + "管理員贈送了你道具."));
                        Eva.LogServerAppend(temp.getName() + "道具已產生.", "請與玩家確認");
                    } else {
                        L1ItemInstance item1 = null;
                        for (createCount = 0; createCount < count; createCount++) {
                            item1 = ItemTable.get().createItem(itemid);
                            item1.setEnchantLevel(enchant);
                            if (pc.getInventory().checkAddItem(item, 1) == L1Inventory.OK) {
                                pc.getInventory().storeItem(item);
                            } else {
                                break;
                            }
                        }
                        if (createCount > 0) {
                            pc.sendPackets(new S_SystemMessage(temp.getName() + "(을)를" + createCount + "개 생성했습니다."));
                            Eva.LogServerAppend(temp.getName() + "을(를)" + createCount + "개 생성했습니다.", "확인바람");
                        }
                    }
                } else if (temp == null) {
                    Eva.LogServerAppend("[贈送禮物 失敗] 該物品編號不存在.", "請仔細確認道具是否存在.");
                }
            }
        }
        JOptionPane.showMessageDialog(this, name2 + "에게 " + temp.getName() + "  禮物   " + ".0 " + ".0 " + ".0.", " Server Message", javax.swing.JOptionPane.INFORMATION_MESSAGE);
        this.setVisible(false);
    }
    // End of variables declaration//GEN-END:variables
}