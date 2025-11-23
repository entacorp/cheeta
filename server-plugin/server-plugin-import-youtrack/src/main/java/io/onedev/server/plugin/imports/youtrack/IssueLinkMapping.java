package io.cheeta.server.plugin.imports.youtrack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import edu.emory.mathcs.backport.java.util.Collections;
import io.cheeta.server.Cheeta;
import io.cheeta.server.service.LinkSpecService;
import io.cheeta.server.model.LinkSpec;
import io.cheeta.server.annotation.ChoiceProvider;
import io.cheeta.server.annotation.Editable;

@Editable
public class IssueLinkMapping implements Serializable {

	private static final long serialVersionUID = 1L;

	private String youTrackIssueLink;
	
	private String oneDevIssueLink;

	@Editable(order=100, name="YouTrack Issue Link")
	@NotEmpty
	public String getYouTrackIssueLink() {
		return youTrackIssueLink;
	}

	public void setYouTrackIssueLink(String youTrackIssueLink) {
		this.youTrackIssueLink = youTrackIssueLink;
	}

	@Editable(order=200, name="Cheeta Issue Link")
	@ChoiceProvider("getCheetaIssueLinkChoices")
	@NotEmpty
	public String getCheetaIssueLink() {
		return oneDevIssueLink;
	}

	public void setCheetaIssueLink(String oneDevIssueLink) {
		this.oneDevIssueLink = oneDevIssueLink;
	}

	@SuppressWarnings("unused")
	private static List<String> getCheetaIssueLinkChoices() {
		List<String> choices = new ArrayList<>();
		List<LinkSpec> linkSpecs = Cheeta.getInstance(LinkSpecService.class).query();
		Collections.sort(linkSpecs, new Comparator<LinkSpec>() {

			@Override
			public int compare(LinkSpec o1, LinkSpec o2) {
				return o1.getName().compareTo(o2.getName());
			}
			
		});
		for (LinkSpec linkSpec: linkSpecs)
			choices.add(linkSpec.getName());
		return choices;
	}
	
}
