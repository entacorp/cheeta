package io.cheeta.server.service;

import io.cheeta.server.model.Build;
import io.cheeta.server.model.BuildLabel;

import java.util.List;

public interface BuildLabelService extends EntityLabelService<BuildLabel> {

	void create(BuildLabel buildLabel);

	void populateLabels(List<Build> builds);
}
