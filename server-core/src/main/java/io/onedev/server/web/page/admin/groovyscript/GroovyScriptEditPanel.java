package io.cheeta.server.web.page.admin.groovyscript;

import static io.cheeta.server.web.translation.Translation._T;

import java.util.List;

import org.jspecify.annotations.Nullable;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.cycle.RequestCycle;

import io.cheeta.server.Cheeta;
import io.cheeta.server.data.migration.VersionedXmlDoc;
import io.cheeta.server.service.AuditService;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.model.support.administration.GroovyScript;
import io.cheeta.server.util.Path;
import io.cheeta.server.util.PathNode;
import io.cheeta.server.web.ajaxlistener.ConfirmLeaveListener;
import io.cheeta.server.web.editable.BeanContext;
import io.cheeta.server.web.editable.BeanEditor;

abstract class GroovyScriptEditPanel extends Panel {

	private final int scriptIndex;
	
	public GroovyScriptEditPanel(String id, int scriptIndex) {
		super(id);
	
		this.scriptIndex = scriptIndex;
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		GroovyScript script;
		if (scriptIndex != -1)
			script = SerializationUtils.clone(getScripts().get(scriptIndex));
		else
			script = new GroovyScript();

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
				attributes.getAjaxCallListeners().add(new ConfirmLeaveListener(GroovyScriptEditPanel.this));
			}

			@Override
			public void onClick(AjaxRequestTarget target) {
				onCancel(target);
			}
			
		});
		
		BeanEditor editor = BeanContext.edit("editor", script);
		form.add(editor);
		form.add(new AjaxButton("save") {

			@Override
			protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
				super.onSubmit(target, form);

				if (scriptIndex != -1) { 
					GroovyScript oldScript = getScripts().get(scriptIndex);
					if (!script.getName().equals(oldScript.getName()) && getScript(script.getName()) != null) {
						editor.error(new Path(new PathNode.Named("name")),
								_T("This name has already been used by another script"));
					}
				} else if (getScript(script.getName()) != null) {
					editor.error(new Path(new PathNode.Named("name")),
							_T("This name has already been used by another script"));
				}

				if (editor.isValid()) {
					var newAuditContent = VersionedXmlDoc.fromBean(script).toXML();
					String oldAuditContent = null;					
					String verb;
					if (scriptIndex != -1) {
						var oldScript = getScripts().set(scriptIndex, script);
						oldAuditContent = VersionedXmlDoc.fromBean(oldScript).toXML();
						verb = "changed";
					} else {
						getScripts().add(script);
						verb = "added";
					}
					Cheeta.getInstance(SettingService.class).saveGroovyScripts(getScripts());
					Cheeta.getInstance(AuditService.class).audit(null, verb + " groovy script \"" + script.getName() + "\"", oldAuditContent, newAuditContent);
					onSave(target);
				} else {
					target.add(form);
				}
			}
			
		});
		
		form.add(new AjaxLink<Void>("cancel") {

			@Override
			protected void updateAjaxAttributes(AjaxRequestAttributes attributes) {
				super.updateAjaxAttributes(attributes);
				attributes.getAjaxCallListeners().add(new ConfirmLeaveListener(GroovyScriptEditPanel.this));
			}

			@Override
			public void onClick(AjaxRequestTarget target) {
				onCancel(target);
			}
			
		});
		form.setOutputMarkupId(true);
		
		add(form);
	}
	
	@Nullable
	private GroovyScript getScript(String name) {
		for (GroovyScript script: getScripts()) {
			if (script.getName().equals(name))
				return script;
		}
		return null;
	}

	protected abstract List<GroovyScript> getScripts();
	
	protected abstract void onSave(AjaxRequestTarget target);
	
	protected abstract void onCancel(AjaxRequestTarget target);

}
