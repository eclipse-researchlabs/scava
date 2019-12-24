package org.eclipse.scava.metricprovider.trans.documentation.readability;

import java.util.Arrays;
import java.util.List;

import org.eclipse.scava.metricprovider.trans.documentation.DocumentationTransMetricProvider;
import org.eclipse.scava.metricprovider.trans.documentation.detectingcode.DocumentationDetectingCodeTransMetricProvider;
import org.eclipse.scava.metricprovider.trans.documentation.detectingcode.model.DocumentationDetectingCodeTransMetric;
import org.eclipse.scava.metricprovider.trans.documentation.detectingcode.model.DocumentationEntryDetectingCode;
import org.eclipse.scava.metricprovider.trans.documentation.model.Documentation;
import org.eclipse.scava.metricprovider.trans.documentation.model.DocumentationTransMetric;
import org.eclipse.scava.metricprovider.trans.documentation.readability.model.DocumentationEntryReadability;
import org.eclipse.scava.metricprovider.trans.documentation.readability.model.DocumentationReadabilityTransMetric;
import org.eclipse.scava.metricprovider.trans.indexing.preparation.IndexPreparationTransMetricProvider;
import org.eclipse.scava.metricprovider.trans.indexing.preparation.model.IndexPrepTransMetric;
import org.eclipse.scava.nlp.tools.readability.Readability;
import org.eclipse.scava.platform.IMetricProvider;
import org.eclipse.scava.platform.ITransientMetricProvider;
import org.eclipse.scava.platform.MetricProviderContext;
import org.eclipse.scava.platform.delta.ProjectDelta;
import org.eclipse.scava.platform.delta.communicationchannel.PlatformCommunicationChannelManager;
import org.eclipse.scava.platform.delta.vcs.PlatformVcsManager;
import org.eclipse.scava.repository.model.CommunicationChannel;
import org.eclipse.scava.repository.model.Project;
import org.eclipse.scava.repository.model.VcsRepository;
import org.eclipse.scava.repository.model.documentation.gitbased.DocumentationGitBased;
import org.eclipse.scava.repository.model.documentation.systematic.DocumentationSystematic;

import com.mongodb.DB;

public class DocumentationReadabilityTransMetricProvider implements ITransientMetricProvider<DocumentationReadabilityTransMetric> {

	protected PlatformVcsManager platformVcsManager;
	protected PlatformCommunicationChannelManager communicationChannelManager;
	
	protected List<IMetricProvider> uses;
	protected MetricProviderContext context;
	
	@Override
	public String getIdentifier() {
		return DocumentationReadabilityTransMetricProvider.class.getCanonicalName();
	}

	@Override
	public String getShortIdentifier() {
		return "trans.documentation.readability";
	}

	@Override
	public String getFriendlyName() {
		return "Documentation calculation of readability";
	}

	@Override
	public String getSummaryInformation() {
		return "This metric calculates the readability of the each documentation entry.";
	}

	@Override
	public boolean appliesTo(Project project) {
		for(VcsRepository repository : project.getVcsRepositories())
			if(repository instanceof DocumentationGitBased) return true;
		for (CommunicationChannel communicationChannel: project.getCommunicationChannels())
			if (communicationChannel instanceof DocumentationSystematic) return true;
		return false;
	}

	@Override
	public void setUses(List<IMetricProvider> uses) {
		this.uses=uses;
	}

	@Override
	public List<String> getIdentifiersOfUses() {
		return Arrays.asList(IndexPreparationTransMetricProvider.class.getCanonicalName(),DocumentationTransMetricProvider.class.getCanonicalName(), DocumentationDetectingCodeTransMetricProvider.class.getCanonicalName());
	}

	@Override
	public void setMetricProviderContext(MetricProviderContext context) {
		this.context=context;
		this.platformVcsManager=context.getPlatformVcsManager();
		this.communicationChannelManager= context.getPlatformCommunicationChannelManager();
	}

	@Override
	public DocumentationReadabilityTransMetric adapt(DB db) {
		return new DocumentationReadabilityTransMetric(db);
	}

	@Override
	public void measure(Project project, ProjectDelta delta, DocumentationReadabilityTransMetric db) {
		
		//This is for the indexing
		IndexPrepTransMetric indexPrepTransMetric = ((IndexPreparationTransMetricProvider)uses.get(0)).adapt(context.getProjectDB(project));	
		indexPrepTransMetric.getExecutedMetricProviders().first().getMetricIdentifiers().add(getIdentifier());
		indexPrepTransMetric.sync();
		
		DocumentationEntryReadability documentationEntryReadability;
		
		DocumentationTransMetric documentationProcess = ((DocumentationTransMetricProvider)uses.get(1)).adapt(context.getProjectDB(project));
		
		for(Documentation documentation : documentationProcess.getDocumentation())
		{
			//We check that the information regarding the documentation concerns the date of analysis
			if(documentation.getLastUpdateDate().equals(delta.getDate().toString()))
			{
				for(String entryId : documentation.getRemovedEntriesId())
				{
					documentationEntryReadability = findDocumentationEntryReadability(db, documentation.getDocumentationId(), entryId);
					db.getDocumentationEntriesReadability().remove(documentationEntryReadability);
				}
				db.sync();
			}
		}
		
		DocumentationDetectingCodeTransMetric documentationDetectingCode = ((DocumentationDetectingCodeTransMetricProvider)uses.get(2)).adapt(context.getProjectDB(project));
		Iterable<DocumentationEntryDetectingCode> documentationEntriesDetectingCode = documentationDetectingCode.getDocumentationEntriesDetectingCode();
			
		for(DocumentationEntryDetectingCode documentationEntry : documentationEntriesDetectingCode)
		{
			if(!documentationEntry.getNaturalLanguage().isEmpty())
			{
				documentationEntryReadability = findDocumentationEntryReadability(db, documentationEntry);
				if(documentationEntryReadability==null)
				{
					documentationEntryReadability= new DocumentationEntryReadability();
					documentationEntryReadability.setEntryId(documentationEntry.getEntryId());
					documentationEntryReadability.setDocumentationId(documentationEntry.getDocumentationId());
					db.getDocumentationEntriesReadability().add(documentationEntryReadability);
				}
				documentationEntryReadability.setReadability(Readability.calculateDaleChall(documentationEntry.getNaturalLanguage()));
				db.sync();
			}
			
		}
		
		

	}
	
	private DocumentationEntryReadability findDocumentationEntryReadability (DocumentationReadabilityTransMetric db, DocumentationEntryDetectingCode documentationEntry)
	{
		return findDocumentationEntryReadability(db, documentationEntry.getDocumentationId(), documentationEntry.getEntryId());
	}
	
	private DocumentationEntryReadability findDocumentationEntryReadability (DocumentationReadabilityTransMetric db, String documentationId, String entryId)
	{
		DocumentationEntryReadability documentationEntryReadability = null;
		Iterable<DocumentationEntryReadability> documentationEntryRIt = db.getDocumentationEntriesReadability().
				find(DocumentationEntryReadability.DOCUMENTATIONID.eq(documentationId),
						DocumentationEntryReadability.ENTRYID.eq(entryId));
		for(DocumentationEntryReadability der : documentationEntryRIt)
			documentationEntryReadability=der;
		return documentationEntryReadability;
	}

}
