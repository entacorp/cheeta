package io.cheeta.server.web.editable.buildspec.step;

import io.cheeta.server.buildspec.BuildSpecAware;
import io.cheeta.server.buildspec.ParamSpecAware;
import io.cheeta.server.buildspec.step.Step;
import io.cheeta.server.web.component.modal.ModalPanel;
import io.cheeta.server.web.editable.BeanDescriptor;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.lang.reflect.InvocationTargetException;

abstract class StepEditModalPanel extends ModalPanel implements BuildSpecAware, ParamSpecAware {

	private Step step;
	
	public StepEditModalPanel(AjaxRequestTarget target, Step step) {
		super(target);
		this.step = step;
	}

	@Override
	protected Component newContent(String id) {
		return new StepEditContentPanel(id, step) {

			@Override
			protected void onSave(AjaxRequestTarget target) {
				StepEditModalPanel.this.onSave(target, step);
			}

			@Override
			protected void onCancel(AjaxRequestTarget target) {
				close();
			}

			@Override
			protected void onSelect(AjaxRequestTarget target, Class<? extends Step> stepType) {
				Step newStep;
				try {
					newStep = stepType.getDeclaredConstructor().newInstance();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						 | InvocationTargetException | NoSuchMethodException | SecurityException e) {
					throw new RuntimeException(e);
				}
				new BeanDescriptor(Step.class).copyProperties(step, newStep);
				step = newStep;
				
				var content = newContent(id);
				replaceWith(content);
				target.add(content);
				target.appendJavaScript(String.format(
						"cheeta.server.form.markDirty($('#%s').find('form'));", 
						StepEditModalPanel.this.getMarkupId()));
			}

		}.setOutputMarkupId(true);
	}

	protected abstract void onSave(AjaxRequestTarget target, Step step);
	
}
