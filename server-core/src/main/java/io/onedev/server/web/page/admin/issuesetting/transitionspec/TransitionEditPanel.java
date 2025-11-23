package io.cheeta.server.web.page.admin.issuesetting.transitionspec;

import java.util.List;

import org.jspecify.annotations.Nullable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.cycle.RequestCycle;

import io.cheeta.server.Cheeta;
import io.cheeta.server.buildspecmodel.inputspec.InputContext;
import io.cheeta.server.buildspecmodel.inputspec.InputSpec;
import io.cheeta.server.data.migration.VersionedXmlDoc;
import io.cheeta.server.service.AuditService;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.model.support.administration.GlobalIssueSetting;
import io.cheeta.server.model.support.issue.transitionspec.TransitionSpec;
import io.cheeta.server.web.ajaxlistener.ConfirmLeaveListener;
import io.cheeta.server.web.editable.BeanContext;
import io.cheeta.server.web.editable.BeanEditor;

abstract class TransitionEditPanel extends Panel implements InputContext {

	private final int transitionIndex;

	private TransitionSpec transition;
	
	public TransitionEditPanel(String id, int transitionIndex, @Nullable TransitionSpec transition) {
		super(id);
	
		this.transitionIndex = transitionIndex;
		this.transition = transition;
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
				
		var bean = new TransitionEditBean();
		bean.setTransitionSpec(transition);
		
		Form<?> form = new Form<Void>("form") {

			@Override
			protected void onError() {
				super.onError();
				RequestCycle.get().find(AjaxRequestTarget.class).add(this);
			}
			
		};
		
		form.add(new AjaxLink<Void>("close") {

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(new ConfirmLeaveListener(TransitionEditPanel.this));
			}

			@Override
			public void onClick(AjaxRequestTarget target) {
				onCancel(target);
			}
			
		});
		
		BeanEditor editor = BeanContext.edit("editor", bean);
		form.add(editor);
		form.add(new AjaxButton("save") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				super.onSubmit(target, form);

				var transition = bean.getTransitionSpec();
				String oldAuditContent = null;
				if (transitionIndex != -1) {
					var oldTransition = getSetting().getTransitionSpecs().set(transitionIndex, transition);
					oldAuditContent = VersionedXmlDoc.fromBean(oldTransition).toXML();
				} else {
					getSetting().getTransitionSpecs().add(transition);
				}
				var newAuditContent = VersionedXmlDoc.fromBean(transition).toXML();
				Cheeta.getInstance(SettingService.class).saveIssueSetting(getSetting());
				var verb = transitionIndex != -1 ? "changed" : "added";
				Cheeta.getInstance(AuditService.class).audit(null, verb + " issue transition", oldAuditContent, newAuditContent);
				onSave(target);
			}
			
		});
		
		form.add(new AjaxLink<Void>("cancel") {

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(new ConfirmLeaveListener(TransitionEditPanel.this));
			}

			@Override
			public void onClick(AjaxRequestTarget target) {
				onCancel(target);
			}
			
		});
		form.setOutputMarkupId(true);
		
		add(form);
	}

	private GlobalIssueSetting getIssueSetting() {
		return Cheeta.getInstance(SettingService.class).getIssueSetting();
	}
	
	@Override
	public List<String> getInputNames() {
		return getIssueSetting().getFieldNames();
	}
	
	@Override
	public InputSpec getInputSpec(String inputName) {
		return getIssueSetting().getFieldSpec(inputName);
	}
	
	protected abstract GlobalIssueSetting getSetting();
	
	protected abstract void onSave(AjaxRequestTarget target);
	
	protected abstract void onCancel(AjaxRequestTarget target);

}
