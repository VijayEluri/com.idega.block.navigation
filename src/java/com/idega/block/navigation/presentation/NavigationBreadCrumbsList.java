/*
 * $Id: NavigationBreadCrumbsList.java,v 1.19 2009/01/14 09:29:49 valdas Exp $
 * Created on Dec 28, 2005
 *
 * Copyright (C) 2005 Idega Software hf. All Rights Reserved.
 *
 * This software is the proprietary information of Idega hf.
 * Use is subject to license terms.
 */
package com.idega.block.navigation.presentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.context.FacesContext;

import com.idega.block.navigation.bean.NavigationListBean;
import com.idega.block.navigation.utils.NavigationConstants;
import com.idega.builder.business.PageTreeNode;
import com.idega.core.builder.business.BuilderService;
import com.idega.core.builder.data.ICPage;
import com.idega.presentation.IWContext;
import com.idega.presentation.text.Link;
import com.idega.presentation.text.ListItem;
import com.idega.presentation.text.Lists;
import com.idega.presentation.text.Text;
import com.idega.util.CoreConstants;

@Deprecated
/* @deprecated Use <code>BreadCrumbs</code> instead */
public class NavigationBreadCrumbsList extends NavigationBlock {

	//private static final String SPACE = "&nbsp;>&nbsp;";

	private String ID = null;
	private ICPage rootPage = null;
	private boolean showRoot = true;
	private boolean ignoreCategoryPages = false;
	
	@Override
	public void main(IWContext iwc) throws Exception {
		BuilderService iBuilderService = getBuilderService(iwc);

		int rootPageID = -1;
		if (this.rootPage == null) {
			rootPageID = iBuilderService.getRootPageId();
		}
		else {
			rootPageID = new Integer(this.rootPage.getPrimaryKey().toString()).intValue();
		}
		int currentPageID = iBuilderService.getCurrentPageId(iwc);
		
		
		PageTreeNode page = new PageTreeNode(currentPageID, iwc);
		boolean showPage = true;
		List<NavigationListBean> pages = new ArrayList<NavigationListBean>();
		while (showPage) {
			if (page.getNodeID() == rootPageID) {
				showPage = false;
			}
			
			if (!(this.ignoreCategoryPages && page.isCategory())) {
				if (page.getNodeID() == currentPageID) {
					Text pageText = new Text(page.getLocalizedNodeName(iwc));
					pages.add(new NavigationListBean(page.getId(), pageText, page.isHiddenInMenu()));
				}
				else {
					Link pageLink = new Link(page.getLocalizedNodeName(iwc));
					pageLink.setText(pageLink.getText());
					pageLink.setPage(page.getNodeID());
					
					setAsCategoryPage(page, pageLink);
					
					pages.add(new NavigationListBean(page.getId(), pageLink, page.isHiddenInMenu()));
				}
			}
			
			page = (PageTreeNode) page.getParentNode();
			if (page == null) {
				showPage = false;
			}
		}
		
		Collections.reverse(pages);
		
		Lists list = new Lists();
		if (this.ID != null) {
			list.setId(this.ID + "_list");
		}
		boolean first = true;
		NavigationListBean bean = null;
		for (int i = 0; i < pages.size(); i++) {
			bean = pages.get(i);
			
			ListItem li = new ListItem();
			if (first) {
				first = false;
				li.setStyleClass("firstPage");
			}
			if ((i + 1) == pages.size()) {
				li.setStyleClass("lastPage");
			}
			
			if (bean.isHiddenInMenu()) {
				li.setStyleClass(CoreConstants.HIDDEN_PAGE_IN_MENU_STYLE_CLASS);
			}
			
			li.add(bean.getObject());
			list.add(li);
			
			if ((i + 1) < pages.size()) {
				li = new ListItem();
				li.setStyleClass("divider");
				li.add(new Text("&gt;"));
				list.add(li);
			}
		}
		
		add(list);
	}
	
	public void setRootPage(ICPage page) {
		this.rootPage = page;
	}
	
	public void setShowRootPage(boolean show) {
		this.showRoot = show;
	}
	
	public void setIgnoreCategoryPages(boolean hide) {
		this.ignoreCategoryPages = hide;
	}
	
	@Override
	public void setId(String Id) {
		super.setId(Id);
		this.ID = Id;
	}
	
	@Override
	public String getBundleIdentifier() {
		return NavigationConstants.IW_BUNDLE_IDENTIFIER;
	}
	
	@Override
	public Object saveState(FacesContext ctx) {
		Object values[] = new Object[5];
		values[0] = super.saveState(ctx);
		values[1] = new Boolean(this.ignoreCategoryPages);
		values[2] = this.rootPage;
		values[3] = new Boolean(this.showRoot);
		values[4] = this.ID;
		
		return values;
	}
	
	@Override
	public void restoreState(FacesContext ctx, Object state) {
		Object values[] = (Object[])state;
		super.restoreState(ctx, values[0]);
		this.ignoreCategoryPages = ((Boolean) values[1]).booleanValue();
		this.rootPage = (ICPage) values[2];
		this.showRoot = ((Boolean) values[3]).booleanValue();
		this.ID = (String) values[4];
	}
}