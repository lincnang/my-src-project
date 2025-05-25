package com.lineage.server.templates;

import java.util.ArrayList;
import java.util.List;

/**
 * 火神合成系統
 *
 * @author sudawei
 */
public class L1ItemUpgrade { // src016
    private int npcId;
    private String actionId;
    private String actionName;
    private int upgradeChance;
    private int deleteChance;
    private int newItemId;
    private int mainItemId;
    private int mainItemCount;
    private String needItemId;
    private String needCounts;
    private String plusItemId;
    private String plusCounts;
    private String plusAddChance;
    private String showSuccessHtmlId;
    private String showFailureHtmlId;
    private String showFailureDeleteHtmlId;
    private int producer;
    private List<Integer> needItemIdList;
    private List<Integer> needCountsList;
    private List<Integer> plusItemIdList;
    private List<Integer> plusCountsList;
    private List<Integer> plusAddChanceList;

    public int getNpcId() {
        return this.npcId;
    }

    public void setNpcId(int npcId) {
        this.npcId = npcId;
    }

    public String getActionId() {
        return this.actionId;
    }

    public void setActionId(String actionId) {
        this.actionId = actionId;
    }

    public String getActionName() {
        return this.actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public int getUpgradeChance() {
        return this.upgradeChance;
    }

    public void setUpgradeChance(int upgradeChance) {
        this.upgradeChance = upgradeChance;
    }

    public int getDeleteChance() {
        return this.deleteChance;
    }

    public void setDeleteChance(int deleteChance) {
        this.deleteChance = deleteChance;
    }

    public int getNewItemId() {
        return this.newItemId;
    }

    public void setNewItemId(int newItemId) {
        this.newItemId = newItemId;
    }

    public int getMainItemId() {
        return this.mainItemId;
    }

    public void setMainItemId(int mainItemId) {
        this.mainItemId = mainItemId;
    }

    public int getMainItemCount() {
        return this.mainItemCount;
    }

    public void setMainItemCount(int mainItemCount) {
        this.mainItemCount = mainItemCount;
    }

    public String getNeedItemId() {
        return this.needItemId;
    }

    public void setNeedItemId(String needItemId) {
        this.needItemId = needItemId;
    }

    public String getNeedCounts() {
        return this.needCounts;
    }

    public void setNeedCounts(String needCounts) {
        this.needCounts = needCounts;
    }

    public String getPlusItemId() {
        return this.plusItemId;
    }

    public void setPlusItemId(String plusItemId) {
        this.plusItemId = plusItemId;
    }

    public String getPlusCounts() {
        return this.plusCounts;
    }

    public void setPlusCounts(String plusCounts) {
        this.plusCounts = plusCounts;
    }

    public String getPlusAddChance() {
        return this.plusAddChance;
    }

    public void setPlusAddChance(String plusAddChance) {
        this.plusAddChance = plusAddChance;
    }

    public String getShowSuccessHtmlId() {
        return this.showSuccessHtmlId;
    }

    public void setShowSuccessHtmlId(String showSuccessHtmlId) {
        this.showSuccessHtmlId = showSuccessHtmlId;
    }

    public String getShowFailureHtmlId() {
        return this.showFailureHtmlId;
    }

    public void setShowFailureHtmlId(String showFailureHtmlId) {
        this.showFailureHtmlId = showFailureHtmlId;
    }

    public String getShowFailureDeleteHtmlId() {
        return this.showFailureDeleteHtmlId;
    }

    public void setShowFailureDeleteHtmlId(String showFailureDeleteHtmlId) {
        this.showFailureDeleteHtmlId = showFailureDeleteHtmlId;
    }

    public int getProducer() {
        return this.producer;
    }

    public void setProducer(int producer) {
        this.producer = producer;
    }

    public List<Integer> getNeedItemIdList() {
        this.needItemIdList = new ArrayList<>();
        if ((this.needItemId == null) || (this.needItemId.isEmpty())) {
            return this.needItemIdList;
        }
        String[] needItemIdArray = this.needItemId.split(",");
        for (String itemId : needItemIdArray) {
            this.needItemIdList.add(Integer.parseInt(itemId));
        }
        return this.needItemIdList;
    }

    public List<Integer> getNeedCountsList() {
        this.needCountsList = new ArrayList<>();
        if ((this.needCounts == null) || (this.needCounts.isEmpty())) {
            return this.needCountsList;
        }
        String[] needCountsArray = this.needCounts.split(",");
        for (String itemId : needCountsArray) {
            this.needCountsList.add(Integer.parseInt(itemId));
        }
        return this.needCountsList;
    }

    public List<Integer> getPlusItemIdList() {
        this.plusItemIdList = new ArrayList<>();
        if ((this.plusItemId == null) || (this.plusItemId.isEmpty())) {
            return this.plusItemIdList;
        }
        String[] plusItemIdArray = this.plusItemId.split(",");
        for (String itemId : plusItemIdArray) {
            this.plusItemIdList.add(Integer.parseInt(itemId));
        }
        return this.plusItemIdList;
    }

    public List<Integer> getPlusCountsList() {
        this.plusCountsList = new ArrayList<>();
        if ((this.plusCounts == null) || (this.plusCounts.isEmpty())) {
            return this.plusCountsList;
        }
        String[] plusCountsArray = this.plusCounts.split(",");
        for (String itemId : plusCountsArray) {
            this.plusCountsList.add(Integer.parseInt(itemId));
        }
        return this.plusCountsList;
    }

    public List<Integer> getPlusAddChanceList() {
        this.plusAddChanceList = new ArrayList<>();
        if ((this.plusAddChance == null) || (this.plusAddChance.isEmpty())) {
            return this.plusAddChanceList;
        }
        String[] plusAddChanceArray = this.plusAddChance.split(",");
        for (String itemId : plusAddChanceArray) {
            this.plusAddChanceList.add(Integer.parseInt(itemId));
        }
        return this.plusAddChanceList;
    }
}
