package com.lineage.server.datatables.storage;

import com.lineage.server.templates.L1AuctionBoardTmp;

import java.util.Map;

public abstract interface AuctionBoardStorage {
    public abstract void load();

    public abstract Map<Integer, L1AuctionBoardTmp> getAuctionBoardTableList();

    public abstract L1AuctionBoardTmp getAuctionBoardTable(int paramInt);

    public abstract void insertAuctionBoard(L1AuctionBoardTmp paramL1AuctionBoardTmp);

    public abstract void updateAuctionBoard(L1AuctionBoardTmp paramL1AuctionBoardTmp);

    public abstract void deleteAuctionBoard(int paramInt);
}
/*
 * Location: C:\Users\kenny\Downloads\奧茲之戰\Server_Game.jar Qualified Name:
 * com.lineage.server.datatables.storage.AuctionBoardStorage JD-Core Version:
 * 0.6.2
 */