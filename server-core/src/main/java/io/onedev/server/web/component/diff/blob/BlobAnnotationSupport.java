package io.cheeta.server.web.component.diff.blob;

import io.cheeta.commons.utils.PlanarRange;
import io.cheeta.server.codequality.CodeProblem;
import io.cheeta.server.codequality.CoverageStatus;
import io.cheeta.server.model.CodeComment;
import io.cheeta.server.util.Pair;
import io.cheeta.server.web.util.DiffPlanarRange;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

import org.jspecify.annotations.Nullable;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

public interface BlobAnnotationSupport extends Serializable {

	@Nullable
	DiffPlanarRange getMarkRange();

	String getMarkUrl(DiffPlanarRange markRange);

	Map<CodeComment, PlanarRange> getOldComments();

	Map<CodeComment, PlanarRange> getNewComments();

	Collection<CodeProblem> getOldProblems();

	Collection<CodeProblem> getNewProblems();

	Map<Integer, CoverageStatus> getOldCoverages();

	Map<Integer, CoverageStatus> getNewCoverages();

	DiffPlanarRange getCommentRange(CodeComment comment);

	@Nullable
	Pair<CodeComment, DiffPlanarRange> getOpenComment();

	void onOpenComment(AjaxRequestTarget target, CodeComment comment, DiffPlanarRange commentRange);

	void onAddComment(AjaxRequestTarget target, DiffPlanarRange commentRange);

	Component getCommentContainer();

}
