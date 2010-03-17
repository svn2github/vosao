/**
 * Vosao CMS. Simple CMS for Google App Engine.
 * Copyright (C) 2009 Vosao development team
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 * email: vosao.dev@gmail.com
 */

package org.vosao.business.impl.imex;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.vosao.business.decorators.TreeItemDecorator;
import org.vosao.dao.DaoTaskException;
import org.vosao.entity.FolderEntity;
import org.vosao.utils.FolderUtil;

import com.google.apphosting.api.DatastorePb.GetResponse;

public class SiteExporter extends AbstractExporter {

	public SiteExporter(ExporterFactory factory) {
		super(factory);
	}

	public boolean isSiteContent(final ZipEntry entry)
			throws UnsupportedEncodingException {
		String[] chain = FolderUtil.getPathChain(entry);
		if (chain.length != 1 || !chain[0].equals("content.xml")) {
			return false;
		}
		return true;
	}

	public void exportSite(final ZipOutputStream out) throws IOException {
		saveFile(out, "_users.xml", getUserExporter().createUsersXML());
		saveFile(out, "_groups.xml", getGroupExporter().createGroupsXML());
		saveFile(out, "_config.xml", getConfigExporter().createConfigXML());
		saveFile(out, "_structures.xml", getStructureExporter()
				.createStructuresXML());
		saveFile(out, "_forms.xml", getFormExporter().createFormsXML());
		saveFile(out, "_messages.xml", getMessagesExporter()
				.createMessagesXML());
		saveFile(out, "_plugins.xml", getPluginExporter().createPluginsXML());
		TreeItemDecorator<FolderEntity> page = getBusiness().getFolderBusiness()
				.findFolderByPath(getBusiness().getFolderBusiness().getTree(), 
						"/page");
		if (page != null) {
			getResourceExporter().addResourcesFromFolder(out, page, "page/");
		}
	}

	private void saveFile(final ZipOutputStream out, String name, 
			String content) throws IOException {
		out.putNextEntry(new ZipEntry(name));
		out.write(content.getBytes("UTF-8"));
		out.closeEntry();
	}
	
	public void readSiteContent(final ZipEntry entry, final String xml)
			throws DocumentException, DaoTaskException {
		Document doc = DocumentHelper.parseText(xml);
		Element root = doc.getRootElement();
		for (Iterator<Element> i = root.elementIterator(); i.hasNext();) {
			Element element = i.next();
			if (element.getName().equals("config")) {
				getConfigExporter().readConfigs(element);
			}
			if (element.getName().equals("pages")) {
				getPageExporter().readPages(element);
			}
			if (element.getName().equals("forms")) {
				getFormExporter().readForms(element);
			}
			if (element.getName().equals("users")) {
				getUserExporter().readUsers(element);
			}
			if (element.getName().equals("groups")) {
				getGroupExporter().readGroups(element);
			}
			if (element.getName().equals("folders")) {
				getFolderExporter().readFolders(element);
			}
			if (element.getName().equals("messages")) {
				getMessagesExporter().readMessages(element);
			}
			if (element.getName().equals("structures")) {
				getStructureExporter().readStructures(element);
			}
			if (element.getName().equals("plugins")) {
				getPluginExporter().readPlugins(element);
			}
		}
	}

	PluginExporter getPluginExporter() {
		return getExporterFactory().getPluginExporter();
	}

	MessagesExporter getMessagesExporter() {
		return getExporterFactory().getMessagesExporter();
	}

	StructureExporter getStructureExporter() {
		return getExporterFactory().getStructureExporter();
	}

	FolderExporter getFolderExporter() {
		return getExporterFactory().getFolderExporter();
	}

	UserExporter getUserExporter() {
		return getExporterFactory().getUserExporter();
	}

	GroupExporter getGroupExporter() {
		return getExporterFactory().getGroupExporter();
	}

	ConfigExporter getConfigExporter() {
		return getExporterFactory().getConfigExporter();
	}
	
	PageExporter getPageExporter() {
		return getExporterFactory().getPageExporter();
	}

	FormExporter getFormExporter() {
		return getExporterFactory().getFormExporter();
	}

	ResourceExporter getResourceExporter() {
		return getExporterFactory().getResourceExporter();
	}

	public boolean importSystemFile(ZipEntry entry, String xml) 
			throws DocumentException, DaoTaskException {
		if (entry.getName().equals("_users.xml")) {
			getUserExporter().readUsersFile(xml);
			return true;
		}
		if (entry.getName().equals("_groups.xml")) {
			getGroupExporter().readGroupsFile(xml);
			return true;
		}
		if (entry.getName().equals("_config.xml")) {
			getConfigExporter().readConfigFile(xml);
			return true;
		}
		if (entry.getName().equals("_structures.xml")) {
			getStructureExporter().readStructuresFile(xml);
			return true;
		}
		if (entry.getName().equals("_forms.xml")) {
			getFormExporter().readFormsFile(xml);
			return true;
		}
		if (entry.getName().equals("_messages.xml")) {
			getMessagesExporter().readMessagesFile(xml);
			return true;
		}
		if (entry.getName().equals("_plugins.xml")) {
			getPluginExporter().readPluginsFile(xml);
			return true;
		}
		
		if (entry.getName().endsWith("_folder.xml")) {
			String folderPath = FolderUtil.getFilePath("/" + entry.getName());
			getResourceExporter().readFolderFile(folderPath, xml);
			return true;
		}
		if (entry.getName().endsWith("_template.xml")) {
			return true;
		}
		if (entry.getName().endsWith("_content.xml")) {
			return true;
		}
		if (entry.getName().endsWith("_comments.xml")) {
			return true;
		}
		if (entry.getName().endsWith("_permissions.xml")) {
			return true;
		}
		return false;
	}
	
	
}
