package io.cheeta.server.web.component.issue.authorizations;

import static io.cheeta.server.model.IssueAuthorization.PROP_ISSUE;
import static io.cheeta.server.model.IssueAuthorization.PROP_USER;
import static io.cheeta.server.web.translation.Translation._T;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.Session;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.google.common.collect.Sets;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.IssueAuthorizationService;
import io.cheeta.server.service.UserService;
import io.cheeta.server.model.Issue;
import io.cheeta.server.model.IssueAuthorization;
import io.cheeta.server.model.User;
import io.cheeta.server.persistence.dao.EntityCriteria;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.util.Similarities;
import io.cheeta.server.util.facade.UserCache;
import io.cheeta.server.web.WebConstants;
import io.cheeta.server.web.ajaxlistener.ConfirmClickListener;
import io.cheeta.server.web.behavior.OnTypingDoneBehavior;
import io.cheeta.server.web.component.datatable.DefaultDataTable;
import io.cheeta.server.web.component.select2.Response;
import io.cheeta.server.web.component.select2.ResponseFiller;
import io.cheeta.server.web.component.select2.SelectToActChoice;
import io.cheeta.server.web.component.user.UserAvatar;
import io.cheeta.server.web.component.user.choice.AbstractUserChoiceProvider;
import io.cheeta.server.web.component.user.choice.UserChoiceResourceReference;
import io.cheeta.server.web.page.user.basicsetting.UserBasicSettingPage;

public abstract class IssueAuthorizationsPanel extends Panel {

	private String query;
	
	private DataTable<IssueAuthorization, Void> authorizationsTable;
	
	private SortableDataProvider<IssueAuthorization, Void> dataProvider ;	
	
	public IssueAuthorizationsPanel(String id) {
		super(id);
	}

	private EntityCriteria<IssueAuthorization> getCriteria() {
		EntityCriteria<IssueAuthorization> criteria = 
				EntityCriteria.of(IssueAuthorization.class);
		if (query != null) {
			criteria.createCriteria(PROP_USER).add(Restrictions.or(
					Restrictions.ilike(User.PROP_NAME, query, MatchMode.ANYWHERE), 
					Restrictions.ilike(User.PROP_FULL_NAME, query, MatchMode.ANYWHERE))); 
		} else {
			criteria.setCacheable(true);
		}
		criteria.add(Restrictions.eq(PROP_ISSUE, getIssue()));
		return criteria;
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		TextField<String> searchField;
		
		add(searchField = new TextField<String>("filterUsers", Model.of(query)));
		searchField.add(new OnTypingDoneBehavior(100) {

			@Override
			protected void onTypingDone(AjaxRequestTarget target) {
				query = searchField.getInput();
				if (StringUtils.isBlank(query))
					query = null;
				target.add(authorizationsTable);
			}
			
		});
		
		add(new SelectToActChoice<User>("addNew", new AbstractUserChoiceProvider() {

			@Override
			public void query(String term, int page, Response<User> response) {
				UserCache cache = Cheeta.getInstance(UserService.class).cloneCache();
				
				List<User> users = new ArrayList<>(cache.getUsers());
				users.removeAll(getIssue().getAuthorizedUsers());
				users.sort(cache.comparingDisplayName(Sets.newHashSet()));
				
				users = new Similarities<User>(users) {

					@Override
					public double getSimilarScore(User object) {
						return cache.getSimilarScore(object, term);
					}
					
				};
				
				new ResponseFiller<>(response).fill(users, page, WebConstants.PAGE_SIZE);
			}

		}) {

			@Override
			protected void onInitialize() {
				super.onInitialize();
				
				getSettings().setPlaceholder(_T("Authorize user..."));
				getSettings().setFormatResult("cheeta.server.userChoiceFormatter.formatResult");
				getSettings().setFormatSelection("cheeta.server.userChoiceFormatter.formatSelection");
				getSettings().setEscapeMarkup("cheeta.server.userChoiceFormatter.escapeMarkup");
			}
			
			@Override
			protected void onSelect(AjaxRequestTarget target, User selection) {
				IssueAuthorization authorization = new IssueAuthorization();
				authorization.setIssue(getIssue());
				authorization.setUser(Cheeta.getInstance(UserService.class).load(selection.getId()));
				Cheeta.getInstance(IssueAuthorizationService.class).createOrUpdate(authorization);
				target.add(authorizationsTable);
				Session.get().success(_T("User authorized"));
			}
			
			@Override
			protected void onConfigure() {
				super.onConfigure();
				setVisible(SecurityUtils.isAdministrator());
			}

			@Override
			public void renderHead(IHeaderResponse response) {
				super.renderHead(response);
				
				response.render(JavaScriptHeaderItem.forReference(new UserChoiceResourceReference()));
			}
			
		});			
		
		List<IColumn<IssueAuthorization, Void>> columns = new ArrayList<>();
		
		columns.add(new AbstractColumn<IssueAuthorization, Void>(Model.of(_T("Name"))) {

			@Override
			public void populateItem(Item<ICellPopulator<IssueAuthorization>> cellItem, String componentId,
					IModel<IssueAuthorization> rowModel) {
				User user = rowModel.getObject().getUser();
				Fragment fragment = new Fragment(componentId, "nameFrag", IssueAuthorizationsPanel.this);
				Link<Void> link = new BookmarkablePageLink<Void>("link", UserBasicSettingPage.class, 
						UserBasicSettingPage.paramsOf(user));
				link.add(new UserAvatar("avatar", user));
				link.add(new Label("name", user.getDisplayName()));
				fragment.add(link);
				cellItem.add(fragment);
			}
		});
		
		columns.add(new AbstractColumn<IssueAuthorization, Void>(Model.of("")) {

			@Override
			public void populateItem(Item<ICellPopulator<IssueAuthorization>> cellItem, String componentId,
					IModel<IssueAuthorization> rowModel) {
				Fragment fragment = new Fragment(componentId, "actionFrag", IssueAuthorizationsPanel.this);
				fragment.add(new AjaxLink<Void>("unauthorize") {

					@Override
					public void onClick(AjaxRequestTarget target) {
						IssueAuthorization authorization = rowModel.getObject();
						Cheeta.getInstance(IssueAuthorizationService.class).delete(authorization);
						Session.get().success(MessageFormat.format(_T("User \"{0}\" unauthorized"), authorization.getUser().getDisplayName()));
						
						target.add(authorizationsTable);
					}

					@Override
					protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
						super.updateAjaxAttributes(attributes);
						
						IssueAuthorization authorization = rowModel.getObject();
						String message = MessageFormat.format(_T("Do you really want to unauthorize user \"{0}\"?"), authorization.getUser().getDisplayName());
						attributes.getAjaxCallListeners().add(new ConfirmClickListener(message));
					}

				});
				
				cellItem.add(fragment);
			}
			
		});
		
		dataProvider = new SortableDataProvider<IssueAuthorization, Void>() {

			@Override
			public Iterator<? extends IssueAuthorization> iterator(long first, long count) {
				EntityCriteria<IssueAuthorization> criteria = getCriteria();
				criteria.addOrder(Order.desc(IssueAuthorization.PROP_ID));
				return Cheeta.getInstance(IssueAuthorizationService.class).query(criteria, (int)first,
						(int)count).iterator();
			}

			@Override
			public long size() {
				return Cheeta.getInstance(IssueAuthorizationService.class).count(getCriteria());
			}

			@Override
			public IModel<IssueAuthorization> model(IssueAuthorization object) {
				Long id = object.getId();
				return new LoadableDetachableModel<IssueAuthorization>() {

					@Override
					protected IssueAuthorization load() {
						return Cheeta.getInstance(IssueAuthorizationService.class).load(id);
					}
					
				};
			}
		};
		
		add(authorizationsTable = new DefaultDataTable<IssueAuthorization, Void>(
				"authorizations", columns, dataProvider, WebConstants.PAGE_SIZE, null));
	}
	
	protected abstract Issue getIssue();

}
