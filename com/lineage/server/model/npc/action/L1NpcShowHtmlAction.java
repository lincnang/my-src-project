package com.lineage.server.model.npc.action;

import com.lineage.server.model.Instance.L1PcInstance;
import com.lineage.server.model.L1Object;
import com.lineage.server.model.npc.L1NpcHtml;
import com.lineage.server.utils.IterableElementList;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class L1NpcShowHtmlAction extends L1NpcXmlAction {
    private final String _htmlId;
    private final String[] _args;

    public L1NpcShowHtmlAction(final Element element) {
        super(element);
        this._htmlId = element.getAttribute("HtmlId");
        final NodeList list = element.getChildNodes();
        final ArrayList<String> dataList = new ArrayList<>();
        for (final Element elem : new IterableElementList(list)) {
            if (elem.getNodeName().equalsIgnoreCase("Data")) {
                dataList.add(elem.getAttribute("Value"));
            }
        }
        this._args = dataList.toArray(new String[0]);
    }

    @Override
    public L1NpcHtml execute(final String actionName, final L1PcInstance pc, final L1Object obj, final byte[] args) {
        return new L1NpcHtml(this._htmlId, this._args);
    }

    @Override
    public void execute(String actionName, String npcid) {
        // TODO Auto-generated method stub
    }
}
