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

package org.vosao.service;

import org.jabsorb.JSONRPCBridge;
import org.vosao.service.front.CommentService;
import org.vosao.service.front.FormService;
import org.vosao.service.front.LoginService;
import org.vosao.service.front.SearchService;

public interface FrontService {
	
	void register(JSONRPCBridge bridge);
	void unregister(JSONRPCBridge bridge);

	FormService getFormService();
	void setFormService(FormService bean);

	LoginService getLoginService();
	void setLoginService(LoginService bean);

	CommentService getCommentService();
	void setCommentService(CommentService bean);
	
	SearchService getSearchService();
	void setSearchService(SearchService bean);
}
