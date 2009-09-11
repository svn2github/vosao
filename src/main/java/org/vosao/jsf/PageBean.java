package org.vosao.jsf;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.vosao.business.decorators.PageDecorator;
import org.vosao.entity.PageEntity;
import org.vosao.entity.TemplateEntity;


public class PageBean extends AbstractJSFBean implements Serializable {

	private static final long serialVersionUID = 2L;
	private static Log log = LogFactory.getLog(PageBean.class);
	
	private List<PageEntity> list;
	private PageEntity current;
	private Map<String, Boolean> selected;
	private String id;
	private PageDecorator root;
	private List<PageEntity> children;
	private List<SelectItem> templates;

	public void init() {
		initList();
		current = new PageEntity();
		if (getParentURL() == null || getParentURL().equals("/")) {
			current.setFriendlyURL("/");
		}
		else {
			current.setFriendlyURL(getParentURL() + "/");
		}
		initSelected();
		initDecorator();
		initTemplates();
	}
	
	private void initTemplates() {
		List<TemplateEntity> templateList = getDao().getTemplateDao().select();
		templates = new ArrayList<SelectItem>();
		for (TemplateEntity t : templateList) {
			templates.add(new SelectItem(t.getId(), t.getTitle()));
		}
	}
	
	private void initList() {
		list = getDao().getPageDao().select();
	}
	
	private void initDecorator() {
		root = getBusiness().getPageBusiness().getTree(list);
	}

	private void initSelected() {
		selected = new HashMap<String, Boolean>();
		for (PageEntity page : list) {
			selected.put(page.getId(), false);
		}
	}
	
	private void initChildren() {
		if (current != null) {
			children = getDao().getPageDao().getByParent(current.getId());
		}
	}
	
	public String cancelEdit() {
		return "pretty:pages";
	}
	
	public String update() {
		List<String> errors = getBusiness().getPageBusiness()
			.validateBeforeUpdate(current);
		if (errors.isEmpty()) {
			if (current.getId() == null) {
				current.setParent(getParent());
			}
			getDao().getPageDao().save(current);
			list.add(current);
			initDecorator();
			return "pretty:pages";
		}
		else {
			JSFUtil.addErrorMessages(errors);
			return null;
		}
	}
	
	public String delete() {
		List<String> ids = new ArrayList<String>();
		for (String id : selected.keySet()) {
			if (selected.get(id)) {
				ids.add(id);
			}
		}
		getDao().getPageDao().remove(ids);
		initList();
		return "pretty:pages";
	}
	
	public void edit() {
		if (id != null) {
			current = getDao().getPageDao().getById(id);
			initChildren();
		}
	}
	
	public void list() {
	}
	
	public String addChild() {
		setParent(current.getId());
		setParentURL(current.getFriendlyURL());
		return "pretty:pageCreate";
	}
	
	public void preview() throws IOException {
		JSFUtil.redirect(current.getFriendlyURL());
	}
	
	
	public List<PageEntity> getList() {
		return list;
	}
	
	public boolean isEdit() {
		return current.getId() != null;
	}

	public PageEntity getCurrent() {
		return current;
	}

	public void setCurrent(PageEntity current) {
		this.current = current;
	}

	public Map<String, Boolean> getSelected() {
		return selected;
	}

	public void setSelected(Map<String, Boolean> selected) {
		this.selected = selected;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTree() {
		if (root != null) {
			return renderPageTree(root).toString();
		}
		else {
			return "empty tree";
		}
	}
	
	private static StringBuffer renderPageTree(final PageDecorator page) {
		StringBuffer result = new StringBuffer();
		result.append("<li><a href=\"page/edit/")
			.append(page.getPage().getId())
			.append("\">")
			.append(page.getPage().getTitle())
			.append("</a>");
		if (page.getChildren().size() > 0) {
			result.append("<ul>");
		}
		for (PageDecorator child : page.getChildren()) {
			result.append(renderPageTree(child));
		}
		if (page.getChildren().size() > 0) {
			result.append("</ul>");
		}
		result.append("</li>");
		return result;
	}

	public List<PageEntity> getChildren() {
		return children;
	}

	public void setChildren(List<PageEntity> children) {
		this.children = children;
	}
	
	public boolean isShowChildren() {
		return children != null;
	}

	public String getParent() {
		String name = this.getClass().getName() + "parent";
		return (String)JSFUtil.getSessionObject(name);
	}

	public void setParent(String parent) {
		String name = this.getClass().getName() + "parent";
		JSFUtil.setSessionObject(name, parent);
	}

	public String getParentURL() {
		String name = this.getClass().getName() + "parentURL";
		return (String)JSFUtil.getSessionObject(name);
	}

	public void setParentURL(String parent) {
		String name = this.getClass().getName() + "parentURL";
		JSFUtil.setSessionObject(name, parent);
	}

	public List<SelectItem> getTemplates() {
		return templates;
	}

	public void setTemplates(List<SelectItem> templates) {
		this.templates = templates;
	}
	
	public boolean isEditURL() {
		return !isEdit() || (children != null && children.size() == 0); 
	}
}
