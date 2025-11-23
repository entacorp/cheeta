package io.cheeta.server.buildspec.param.spec.userchoiceparam.defaultvalueprovider;

import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.OmitName;
import io.cheeta.server.annotation.ScriptChoice;
import io.cheeta.server.util.GroovyUtils;

import javax.validation.constraints.NotEmpty;

@Editable(order=400, name="Evaluate script to get default value")
public class ScriptingDefaultValue implements DefaultValueProvider {

	private static final long serialVersionUID = 1L;

	private String scriptName;

	@Editable(description="Groovy script to be evaluated. It should return a <i>string</i> value. "
			+ "Check <a href='https://docs.cheeta.io/appendix/scripting' target='_blank'>scripting help</a> for details")
	@ScriptChoice
	@OmitName
	@NotEmpty
	public String getScriptName() {
		return scriptName;
	}

	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	@Override
	public String getDefaultValue() {
		return (String) GroovyUtils.evalScriptByName(scriptName);
	}

}
