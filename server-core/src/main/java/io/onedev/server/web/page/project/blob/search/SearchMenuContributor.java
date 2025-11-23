package io.cheeta.server.web.page.project.blob.search;

import java.util.List;

import io.cheeta.server.web.component.floating.FloatingPanel;
import io.cheeta.server.web.component.menu.MenuItem;

public interface SearchMenuContributor {
	
	List<MenuItem> getMenuItems(FloatingPanel dropdown);
	
}
