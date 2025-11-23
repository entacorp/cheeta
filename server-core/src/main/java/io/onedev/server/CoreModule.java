package io.cheeta.server;

import static com.google.common.collect.Lists.newArrayList;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.Configuration;
import javax.validation.Path;
import javax.validation.Path.Node;
import javax.validation.TraversableResolver;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.shiro.authc.credential.PasswordService;
import org.apache.shiro.guice.aop.ShiroAopModule;
import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.config.ShiroFilterConfiguration;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.shiro.web.servlet.ShiroFilter;
import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.http.WicketServlet;
import org.eclipse.jetty.server.session.SessionDataStoreFactory;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.model.naming.PhysicalNamingStrategy;
import org.hibernate.collection.internal.PersistentBag;
import org.hibernate.type.Type;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matchers;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.converters.basic.NullConverter;
import com.thoughtworks.xstream.converters.extended.ISO8601DateConverter;
import com.thoughtworks.xstream.converters.extended.ISO8601SqlTimestampConverter;
import com.thoughtworks.xstream.converters.reflection.ReflectionProvider;
import com.thoughtworks.xstream.core.JVM;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import com.vladsch.flexmark.util.misc.Extension;

import io.cheeta.agent.ExecutorUtils;
import io.cheeta.commons.bootstrap.Bootstrap;
import io.cheeta.commons.loader.AbstractPlugin;
import io.cheeta.commons.loader.AbstractPluginModule;
import io.cheeta.commons.utils.ExceptionUtils;
import io.cheeta.commons.utils.StringUtils;
import io.cheeta.k8shelper.KubernetesHelper;
import io.cheeta.k8shelper.OsInfo;
import io.cheeta.server.ai.McpHelperResource;
import io.cheeta.server.annotation.Shallow;
import io.cheeta.server.attachment.AttachmentService;
import io.cheeta.server.attachment.DefaultAttachmentService;
import io.cheeta.server.buildspec.BuildSpecSchemaResource;
import io.cheeta.server.buildspec.job.log.instruction.LogInstruction;
import io.cheeta.server.cluster.ClusterResource;
import io.cheeta.server.codequality.CodeProblemContribution;
import io.cheeta.server.codequality.LineCoverageContribution;
import io.cheeta.server.commandhandler.ApplyDatabaseConstraints;
import io.cheeta.server.commandhandler.BackupDatabase;
import io.cheeta.server.commandhandler.CheckDataVersion;
import io.cheeta.server.commandhandler.CleanDatabase;
import io.cheeta.server.commandhandler.ResetAdminPassword;
import io.cheeta.server.commandhandler.RestoreDatabase;
import io.cheeta.server.commandhandler.Translate;
import io.cheeta.server.commandhandler.Upgrade;
import io.cheeta.server.data.DataService;
import io.cheeta.server.data.DefaultDataService;
import io.cheeta.server.entityreference.DefaultReferenceChangeService;
import io.cheeta.server.entityreference.ReferenceChangeService;
import io.cheeta.server.event.DefaultListenerRegistry;
import io.cheeta.server.event.ListenerRegistry;
import io.cheeta.server.exception.handler.ExceptionHandler;
import io.cheeta.server.git.GitFilter;
import io.cheeta.server.git.GitLfsFilter;
import io.cheeta.server.git.GitLocationProvider;
import io.cheeta.server.git.GoGetFilter;
import io.cheeta.server.git.SshCommandCreator;
import io.cheeta.server.git.hook.GitPostReceiveCallback;
import io.cheeta.server.git.hook.GitPreReceiveCallback;
import io.cheeta.server.git.hook.GitPreReceiveChecker;
import io.cheeta.server.git.location.GitLocation;
import io.cheeta.server.git.service.DefaultGitService;
import io.cheeta.server.git.service.GitService;
import io.cheeta.server.git.signatureverification.DefaultSignatureVerificationService;
import io.cheeta.server.git.signatureverification.SignatureVerificationService;
import io.cheeta.server.git.signatureverification.SignatureVerifier;
import io.cheeta.server.jetty.DefaultJettyService;
import io.cheeta.server.jetty.DefaultSessionDataStoreFactory;
import io.cheeta.server.jetty.JettyService;
import io.cheeta.server.job.DefaultJobService;
import io.cheeta.server.job.DefaultResourceAllocator;
import io.cheeta.server.job.JobService;
import io.cheeta.server.job.ResourceAllocator;
import io.cheeta.server.job.log.DefaultLogService;
import io.cheeta.server.job.log.LogService;
import io.cheeta.server.mail.DefaultMailService;
import io.cheeta.server.mail.MailService;
import io.cheeta.server.markdown.DefaultMarkdownService;
import io.cheeta.server.markdown.HtmlProcessor;
import io.cheeta.server.markdown.MarkdownService;
import io.cheeta.server.model.support.administration.GroovyScript;
import io.cheeta.server.model.support.administration.authenticator.Authenticator;
import io.cheeta.server.notification.BuildNotificationManager;
import io.cheeta.server.notification.CodeCommentNotificationManager;
import io.cheeta.server.notification.CommitNotificationManager;
import io.cheeta.server.notification.IssueNotificationManager;
import io.cheeta.server.notification.PackNotificationManager;
import io.cheeta.server.notification.PullRequestNotificationManager;
import io.cheeta.server.notification.WebHookManager;
import io.cheeta.server.pack.PackFilter;
import io.cheeta.server.persistence.DefaultIdService;
import io.cheeta.server.persistence.DefaultSessionFactoryService;
import io.cheeta.server.persistence.DefaultSessionService;
import io.cheeta.server.persistence.DefaultTransactionService;
import io.cheeta.server.persistence.HibernateInterceptor;
import io.cheeta.server.persistence.IdService;
import io.cheeta.server.persistence.PersistListener;
import io.cheeta.server.persistence.PrefixedNamingStrategy;
import io.cheeta.server.persistence.SessionFactoryProvider;
import io.cheeta.server.persistence.SessionFactoryService;
import io.cheeta.server.persistence.SessionInterceptor;
import io.cheeta.server.persistence.SessionProvider;
import io.cheeta.server.persistence.SessionService;
import io.cheeta.server.persistence.TransactionInterceptor;
import io.cheeta.server.persistence.TransactionService;
import io.cheeta.server.persistence.annotation.Sessional;
import io.cheeta.server.persistence.annotation.Transactional;
import io.cheeta.server.persistence.dao.Dao;
import io.cheeta.server.persistence.dao.DefaultDao;
import io.cheeta.server.persistence.exception.ConstraintViolationExceptionHandler;
import io.cheeta.server.rest.DefaultServletContainer;
import io.cheeta.server.rest.JerseyConfigurator;
import io.cheeta.server.rest.ResourceConfigProvider;
import io.cheeta.server.rest.WebApplicationExceptionHandler;
import io.cheeta.server.rest.resource.ProjectResource;
import io.cheeta.server.search.code.CodeIndexService;
import io.cheeta.server.search.code.CodeSearchService;
import io.cheeta.server.search.code.DefaultCodeIndexService;
import io.cheeta.server.search.code.DefaultCodeSearchService;
import io.cheeta.server.search.entitytext.CodeCommentTextService;
import io.cheeta.server.search.entitytext.DefaultCodeCommentTextService;
import io.cheeta.server.search.entitytext.DefaultIssueTextService;
import io.cheeta.server.search.entitytext.DefaultPullRequestTextService;
import io.cheeta.server.search.entitytext.IssueTextService;
import io.cheeta.server.search.entitytext.PullRequestTextService;
import io.cheeta.server.security.BasicAuthenticationFilter;
import io.cheeta.server.security.BearerAuthenticationFilter;
import io.cheeta.server.security.CodePullAuthorizationSource;
import io.cheeta.server.security.DefaultFilterChainResolver;
import io.cheeta.server.security.DefaultPasswordService;
import io.cheeta.server.security.DefaultRememberMeManager;
import io.cheeta.server.security.DefaultShiroFilterConfiguration;
import io.cheeta.server.security.DefaultWebSecurityManager;
import io.cheeta.server.security.FilterChainConfigurator;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.security.realm.GeneralAuthorizingRealm;
import io.cheeta.server.service.AccessTokenAuthorizationService;
import io.cheeta.server.service.AccessTokenService;
import io.cheeta.server.service.AgentAttributeService;
import io.cheeta.server.service.AgentLastUsedDateService;
import io.cheeta.server.service.AgentService;
import io.cheeta.server.service.AgentTokenService;
import io.cheeta.server.service.AlertService;
import io.cheeta.server.service.BaseAuthorizationService;
import io.cheeta.server.service.BuildDependenceService;
import io.cheeta.server.service.BuildLabelService;
import io.cheeta.server.service.BuildMetricService;
import io.cheeta.server.service.BuildParamService;
import io.cheeta.server.service.BuildQueryPersonalizationService;
import io.cheeta.server.service.BuildService;
import io.cheeta.server.service.CodeCommentMentionService;
import io.cheeta.server.service.CodeCommentQueryPersonalizationService;
import io.cheeta.server.service.CodeCommentReplyService;
import io.cheeta.server.service.CodeCommentService;
import io.cheeta.server.service.CodeCommentStatusChangeService;
import io.cheeta.server.service.CodeCommentTouchService;
import io.cheeta.server.service.CommitQueryPersonalizationService;
import io.cheeta.server.service.DashboardGroupShareService;
import io.cheeta.server.service.DashboardService;
import io.cheeta.server.service.DashboardUserShareService;
import io.cheeta.server.service.DashboardVisitService;
import io.cheeta.server.service.EmailAddressService;
import io.cheeta.server.service.GitLfsLockService;
import io.cheeta.server.service.GpgKeyService;
import io.cheeta.server.service.GroupAuthorizationService;
import io.cheeta.server.service.GroupService;
import io.cheeta.server.service.IssueAuthorizationService;
import io.cheeta.server.service.IssueChangeService;
import io.cheeta.server.service.IssueCommentReactionService;
import io.cheeta.server.service.IssueCommentRevisionService;
import io.cheeta.server.service.IssueCommentService;
import io.cheeta.server.service.IssueDescriptionRevisionService;
import io.cheeta.server.service.IssueFieldService;
import io.cheeta.server.service.IssueLinkService;
import io.cheeta.server.service.IssueMentionService;
import io.cheeta.server.service.IssueQueryPersonalizationService;
import io.cheeta.server.service.IssueReactionService;
import io.cheeta.server.service.IssueScheduleService;
import io.cheeta.server.service.IssueService;
import io.cheeta.server.service.IssueStateHistoryService;
import io.cheeta.server.service.IssueTouchService;
import io.cheeta.server.service.IssueVoteService;
import io.cheeta.server.service.IssueWatchService;
import io.cheeta.server.service.IssueWorkService;
import io.cheeta.server.service.IterationService;
import io.cheeta.server.service.JobCacheService;
import io.cheeta.server.service.LabelSpecService;
import io.cheeta.server.service.LinkAuthorizationService;
import io.cheeta.server.service.LinkSpecService;
import io.cheeta.server.service.MembershipService;
import io.cheeta.server.service.PackBlobReferenceService;
import io.cheeta.server.service.PackBlobService;
import io.cheeta.server.service.PackLabelService;
import io.cheeta.server.service.PackQueryPersonalizationService;
import io.cheeta.server.service.PackService;
import io.cheeta.server.service.PendingSuggestionApplyService;
import io.cheeta.server.service.ProjectLabelService;
import io.cheeta.server.service.ProjectLastEventDateService;
import io.cheeta.server.service.ProjectService;
import io.cheeta.server.service.PullRequestAssignmentService;
import io.cheeta.server.service.PullRequestChangeService;
import io.cheeta.server.service.PullRequestCommentReactionService;
import io.cheeta.server.service.PullRequestCommentRevisionService;
import io.cheeta.server.service.PullRequestCommentService;
import io.cheeta.server.service.PullRequestDescriptionRevisionService;
import io.cheeta.server.service.PullRequestLabelService;
import io.cheeta.server.service.PullRequestMentionService;
import io.cheeta.server.service.PullRequestQueryPersonalizationService;
import io.cheeta.server.service.PullRequestReactionService;
import io.cheeta.server.service.PullRequestReviewService;
import io.cheeta.server.service.PullRequestService;
import io.cheeta.server.service.PullRequestTouchService;
import io.cheeta.server.service.PullRequestUpdateService;
import io.cheeta.server.service.PullRequestWatchService;
import io.cheeta.server.service.ReviewedDiffService;
import io.cheeta.server.service.RoleService;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.service.SshKeyService;
import io.cheeta.server.service.SsoAccountService;
import io.cheeta.server.service.SsoProviderService;
import io.cheeta.server.service.StopwatchService;
import io.cheeta.server.service.UserAuthorizationService;
import io.cheeta.server.service.UserInvitationService;
import io.cheeta.server.service.UserService;
import io.cheeta.server.service.impl.DefaultAccessTokenAuthorizationService;
import io.cheeta.server.service.impl.DefaultAccessTokenService;
import io.cheeta.server.service.impl.DefaultAgentAttributeService;
import io.cheeta.server.service.impl.DefaultAgentLastUsedDateService;
import io.cheeta.server.service.impl.DefaultAgentService;
import io.cheeta.server.service.impl.DefaultAgentTokenService;
import io.cheeta.server.service.impl.DefaultAlertService;
import io.cheeta.server.service.impl.DefaultBaseAuthorizationService;
import io.cheeta.server.service.impl.DefaultBuildDependenceService;
import io.cheeta.server.service.impl.DefaultBuildLabelService;
import io.cheeta.server.service.impl.DefaultBuildMetricService;
import io.cheeta.server.service.impl.DefaultBuildParamService;
import io.cheeta.server.service.impl.DefaultBuildQueryPersonalizationService;
import io.cheeta.server.service.impl.DefaultBuildService;
import io.cheeta.server.service.impl.DefaultCodeCommentMentionService;
import io.cheeta.server.service.impl.DefaultCodeCommentQueryPersonalizationService;
import io.cheeta.server.service.impl.DefaultCodeCommentReplyService;
import io.cheeta.server.service.impl.DefaultCodeCommentService;
import io.cheeta.server.service.impl.DefaultCodeCommentStatusChangeService;
import io.cheeta.server.service.impl.DefaultCodeCommentTouchService;
import io.cheeta.server.service.impl.DefaultCommitQueryPersonalizationService;
import io.cheeta.server.service.impl.DefaultDashboardGroupShareService;
import io.cheeta.server.service.impl.DefaultDashboardService;
import io.cheeta.server.service.impl.DefaultDashboardUserShareService;
import io.cheeta.server.service.impl.DefaultDashboardVisitService;
import io.cheeta.server.service.impl.DefaultEmailAddressService;
import io.cheeta.server.service.impl.DefaultGitLfsLockService;
import io.cheeta.server.service.impl.DefaultGpgKeyService;
import io.cheeta.server.service.impl.DefaultGroupAuthorizationService;
import io.cheeta.server.service.impl.DefaultGroupService;
import io.cheeta.server.service.impl.DefaultIssueAuthorizationService;
import io.cheeta.server.service.impl.DefaultIssueChangeService;
import io.cheeta.server.service.impl.DefaultIssueCommentReactionService;
import io.cheeta.server.service.impl.DefaultIssueCommentRevisionService;
import io.cheeta.server.service.impl.DefaultIssueCommentService;
import io.cheeta.server.service.impl.DefaultIssueDescriptionRevisionService;
import io.cheeta.server.service.impl.DefaultIssueFieldService;
import io.cheeta.server.service.impl.DefaultIssueLinkService;
import io.cheeta.server.service.impl.DefaultIssueMentionService;
import io.cheeta.server.service.impl.DefaultIssueQueryPersonalizationService;
import io.cheeta.server.service.impl.DefaultIssueReactionService;
import io.cheeta.server.service.impl.DefaultIssueScheduleService;
import io.cheeta.server.service.impl.DefaultIssueService;
import io.cheeta.server.service.impl.DefaultIssueStateHistoryService;
import io.cheeta.server.service.impl.DefaultIssueTouchService;
import io.cheeta.server.service.impl.DefaultIssueVoteService;
import io.cheeta.server.service.impl.DefaultIssueWatchService;
import io.cheeta.server.service.impl.DefaultIssueWorkService;
import io.cheeta.server.service.impl.DefaultIterationService;
import io.cheeta.server.service.impl.DefaultJobCacheService;
import io.cheeta.server.service.impl.DefaultLabelSpecService;
import io.cheeta.server.service.impl.DefaultLinkAuthorizationService;
import io.cheeta.server.service.impl.DefaultLinkSpecService;
import io.cheeta.server.service.impl.DefaultMembershipService;
import io.cheeta.server.service.impl.DefaultPackBlobReferenceService;
import io.cheeta.server.service.impl.DefaultPackBlobService;
import io.cheeta.server.service.impl.DefaultPackLabelService;
import io.cheeta.server.service.impl.DefaultPackQueryPersonalizationService;
import io.cheeta.server.service.impl.DefaultPackService;
import io.cheeta.server.service.impl.DefaultPendingSuggestionApplyService;
import io.cheeta.server.service.impl.DefaultProjectLabelService;
import io.cheeta.server.service.impl.DefaultProjectLastEventDateService;
import io.cheeta.server.service.impl.DefaultProjectService;
import io.cheeta.server.service.impl.DefaultPullRequestAssignmentService;
import io.cheeta.server.service.impl.DefaultPullRequestChangeService;
import io.cheeta.server.service.impl.DefaultPullRequestCommentReactionService;
import io.cheeta.server.service.impl.DefaultPullRequestCommentRevisionService;
import io.cheeta.server.service.impl.DefaultPullRequestCommentService;
import io.cheeta.server.service.impl.DefaultPullRequestDescriptionRevisionService;
import io.cheeta.server.service.impl.DefaultPullRequestLabelService;
import io.cheeta.server.service.impl.DefaultPullRequestMentionService;
import io.cheeta.server.service.impl.DefaultPullRequestQueryPersonalizationService;
import io.cheeta.server.service.impl.DefaultPullRequestReactionService;
import io.cheeta.server.service.impl.DefaultPullRequestReviewService;
import io.cheeta.server.service.impl.DefaultPullRequestService;
import io.cheeta.server.service.impl.DefaultPullRequestTouchService;
import io.cheeta.server.service.impl.DefaultPullRequestUpdateService;
import io.cheeta.server.service.impl.DefaultPullRequestWatchService;
import io.cheeta.server.service.impl.DefaultReviewedDiffService;
import io.cheeta.server.service.impl.DefaultRoleService;
import io.cheeta.server.service.impl.DefaultSettingService;
import io.cheeta.server.service.impl.DefaultSshKeyService;
import io.cheeta.server.service.impl.DefaultSsoAccountService;
import io.cheeta.server.service.impl.DefaultSsoProviderService;
import io.cheeta.server.service.impl.DefaultStopwatchService;
import io.cheeta.server.service.impl.DefaultUserAuthorizationService;
import io.cheeta.server.service.impl.DefaultUserInvitationService;
import io.cheeta.server.service.impl.DefaultUserService;
import io.cheeta.server.ssh.CommandCreator;
import io.cheeta.server.ssh.DefaultSshAuthenticator;
import io.cheeta.server.ssh.DefaultSshService;
import io.cheeta.server.ssh.SshAuthenticator;
import io.cheeta.server.ssh.SshService;
import io.cheeta.server.taskschedule.DefaultTaskScheduler;
import io.cheeta.server.taskschedule.TaskScheduler;
import io.cheeta.server.updatecheck.DefaultUpdateCheckService;
import io.cheeta.server.updatecheck.UpdateCheckService;
import io.cheeta.server.util.ScriptContribution;
import io.cheeta.server.util.concurrent.BatchWorkExecutionService;
import io.cheeta.server.util.concurrent.DefaultBatchWorkExecutionService;
import io.cheeta.server.util.concurrent.DefaultWorkExecutionService;
import io.cheeta.server.util.concurrent.WorkExecutionService;
import io.cheeta.server.util.jackson.ObjectMapperConfigurator;
import io.cheeta.server.util.jackson.ObjectMapperProvider;
import io.cheeta.server.util.jackson.git.GitObjectMapperConfigurator;
import io.cheeta.server.util.jackson.hibernate.HibernateObjectMapperConfigurator;
import io.cheeta.server.util.oauth.DefaultOAuthTokenService;
import io.cheeta.server.util.oauth.OAuthTokenService;
import io.cheeta.server.util.xstream.CollectionConverter;
import io.cheeta.server.util.xstream.HibernateProxyConverter;
import io.cheeta.server.util.xstream.MapConverter;
import io.cheeta.server.util.xstream.ObjectMapperConverter;
import io.cheeta.server.util.xstream.ReflectionConverter;
import io.cheeta.server.util.xstream.StringConverter;
import io.cheeta.server.util.xstream.VersionedDocumentConverter;
import io.cheeta.server.validation.MessageInterpolator;
import io.cheeta.server.validation.ShallowValidatorProvider;
import io.cheeta.server.validation.ValidatorProvider;
import io.cheeta.server.web.DefaultUrlService;
import io.cheeta.server.web.DefaultWicketFilter;
import io.cheeta.server.web.DefaultWicketServlet;
import io.cheeta.server.web.ResourcePackScopeContribution;
import io.cheeta.server.web.UrlService;
import io.cheeta.server.web.WebApplication;
import io.cheeta.server.web.avatar.AvatarService;
import io.cheeta.server.web.avatar.DefaultAvatarService;
import io.cheeta.server.web.component.diff.DiffRenderer;
import io.cheeta.server.web.component.markdown.SourcePositionTrackExtension;
import io.cheeta.server.web.component.markdown.emoji.EmojiExtension;
import io.cheeta.server.web.component.taskbutton.TaskButton;
import io.cheeta.server.web.editable.DefaultEditSupportRegistry;
import io.cheeta.server.web.editable.EditSupport;
import io.cheeta.server.web.editable.EditSupportLocator;
import io.cheeta.server.web.editable.EditSupportRegistry;
import io.cheeta.server.web.exceptionhandler.PageExpiredExceptionHandler;
import io.cheeta.server.web.page.layout.AdministrationSettingContribution;
import io.cheeta.server.web.page.project.blob.render.BlobRenderer;
import io.cheeta.server.web.page.project.setting.ProjectSettingContribution;
import io.cheeta.server.web.upload.DefaultUploadService;
import io.cheeta.server.web.upload.UploadService;
import io.cheeta.server.web.websocket.AlertEventBroadcaster;
import io.cheeta.server.web.websocket.BuildEventBroadcaster;
import io.cheeta.server.web.websocket.CodeCommentEventBroadcaster;
import io.cheeta.server.web.websocket.CommitIndexedBroadcaster;
import io.cheeta.server.web.websocket.DefaultWebSocketService;
import io.cheeta.server.web.websocket.IssueEventBroadcaster;
import io.cheeta.server.web.websocket.PullRequestEventBroadcaster;
import io.cheeta.server.web.websocket.WebSocketService;
import io.cheeta.server.xodus.CommitInfoService;
import io.cheeta.server.xodus.DefaultCommitInfoService;
import io.cheeta.server.xodus.DefaultIssueInfoService;
import io.cheeta.server.xodus.DefaultPullRequestInfoService;
import io.cheeta.server.xodus.DefaultVisitInfoService;
import io.cheeta.server.xodus.IssueInfoService;
import io.cheeta.server.xodus.PullRequestInfoService;
import io.cheeta.server.xodus.VisitInfoService;
import nl.altindag.ssl.SSLFactory;

/**
 * NOTE: Do not forget to rename moduleClass property defined in the pom if you've renamed this class.
 *
 */
public class CoreModule extends AbstractPluginModule {

	@Override
	protected void configure() {
		super.configure();
		
		bind(ListenerRegistry.class).to(DefaultListenerRegistry.class);
		bind(JettyService.class).to(DefaultJettyService.class);
		bind(ServletContextHandler.class).toProvider(DefaultJettyService.class);
		
		bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).in(Singleton.class);
		
		bind(ValidatorFactory.class).toProvider(() -> {
			Configuration<?> configuration = Validation
					.byDefaultProvider()
					.configure()
					.messageInterpolator(new MessageInterpolator());
			return configuration.buildValidatorFactory();
		}).in(Singleton.class);

		bind(ValidatorFactory.class).annotatedWith(Shallow.class).toProvider(() -> {
			Configuration<?> configuration = Validation
					.byDefaultProvider()
					.configure()
					.traversableResolver(new TraversableResolver() {

						@Override
						public boolean isReachable(Object traversableObject, Node traversableProperty,
								Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
							return true;
						}
	
						@Override
						public boolean isCascadable(Object traversableObject, Node traversableProperty,
								Class<?> rootBeanType, Path pathToTraversableObject, ElementType elementType) {
							return false;
						}
					})					
					.messageInterpolator(new MessageInterpolator());
			return configuration.buildValidatorFactory();
		}).in(Singleton.class);
		
		bind(Validator.class).toProvider(ValidatorProvider.class).in(Singleton.class);
		bind(Validator.class).annotatedWith(Shallow.class).toProvider(ShallowValidatorProvider.class).in(Singleton.class);

		configurePersistence();
		configureSecurity();
		configureRestful();
		configureWeb();
		configureGit();
		configureBuild();

		/*
		 * Declare bindings explicitly instead of using ImplementedBy annotation as
		 * HK2 to guice bridge can only search in explicit bindings in Guice   
		 */
		bind(SshAuthenticator.class).to(DefaultSshAuthenticator.class);
		bind(SshService.class).to(DefaultSshService.class);
		bind(MarkdownService.class).to(DefaultMarkdownService.class);
		bind(SettingService.class).to(DefaultSettingService.class);
		bind(DataService.class).to(DefaultDataService.class);
		bind(TaskScheduler.class).to(DefaultTaskScheduler.class);
		bind(PullRequestCommentService.class).to(DefaultPullRequestCommentService.class);
		bind(CodeCommentService.class).to(DefaultCodeCommentService.class);
		bind(PullRequestService.class).to(DefaultPullRequestService.class);
		bind(PullRequestUpdateService.class).to(DefaultPullRequestUpdateService.class);
		bind(ProjectService.class).to(DefaultProjectService.class);
		bind(ProjectLastEventDateService.class).to(DefaultProjectLastEventDateService.class);
		bind(UserInvitationService.class).to(DefaultUserInvitationService.class);
		bind(PullRequestReviewService.class).to(DefaultPullRequestReviewService.class);
		bind(BuildService.class).to(DefaultBuildService.class);
		bind(BuildDependenceService.class).to(DefaultBuildDependenceService.class);
		bind(JobService.class).to(DefaultJobService.class);
		bind(JobCacheService.class).to(DefaultJobCacheService.class);
		bind(LogService.class).to(DefaultLogService.class);
		bind(MailService.class).to(DefaultMailService.class);
		bind(IssueService.class).to(DefaultIssueService.class);
		bind(IssueFieldService.class).to(DefaultIssueFieldService.class);
		bind(BuildParamService.class).to(DefaultBuildParamService.class);
		bind(UserAuthorizationService.class).to(DefaultUserAuthorizationService.class);
		bind(GroupAuthorizationService.class).to(DefaultGroupAuthorizationService.class);
		bind(PullRequestWatchService.class).to(DefaultPullRequestWatchService.class);
		bind(RoleService.class).to(DefaultRoleService.class);
		bind(CommitInfoService.class).to(DefaultCommitInfoService.class);
		bind(IssueInfoService.class).to(DefaultIssueInfoService.class);
		bind(VisitInfoService.class).to(DefaultVisitInfoService.class);
		bind(BatchWorkExecutionService.class).to(DefaultBatchWorkExecutionService.class);
		bind(WorkExecutionService.class).to(DefaultWorkExecutionService.class);
		bind(GroupService.class).to(DefaultGroupService.class);
		bind(IssueMentionService.class).to(DefaultIssueMentionService.class);
		bind(PullRequestMentionService.class).to(DefaultPullRequestMentionService.class);
		bind(CodeCommentMentionService.class).to(DefaultCodeCommentMentionService.class);
		bind(MembershipService.class).to(DefaultMembershipService.class);
		bind(PullRequestChangeService.class).to(DefaultPullRequestChangeService.class);
		bind(CodeCommentReplyService.class).to(DefaultCodeCommentReplyService.class);
		bind(CodeCommentStatusChangeService.class).to(DefaultCodeCommentStatusChangeService.class);
		bind(AttachmentService.class).to(DefaultAttachmentService.class);
		bind(PullRequestInfoService.class).to(DefaultPullRequestInfoService.class);
		bind(PullRequestNotificationManager.class);
		bind(CommitNotificationManager.class);
		bind(BuildNotificationManager.class);
		bind(PackNotificationManager.class);
		bind(IssueNotificationManager.class);
		bind(CodeCommentNotificationManager.class);
		bind(CodeCommentService.class).to(DefaultCodeCommentService.class);
		bind(AccessTokenService.class).to(DefaultAccessTokenService.class);
		bind(UserService.class).to(DefaultUserService.class);
		bind(IssueWatchService.class).to(DefaultIssueWatchService.class);
		bind(IssueChangeService.class).to(DefaultIssueChangeService.class);
		bind(IssueVoteService.class).to(DefaultIssueVoteService.class);
		bind(IssueWorkService.class).to(DefaultIssueWorkService.class);
		bind(IterationService.class).to(DefaultIterationService.class);
		bind(IssueCommentService.class).to(DefaultIssueCommentService.class);
		bind(IssueQueryPersonalizationService.class).to(DefaultIssueQueryPersonalizationService.class);
		bind(PullRequestQueryPersonalizationService.class).to(DefaultPullRequestQueryPersonalizationService.class);
		bind(CodeCommentQueryPersonalizationService.class).to(DefaultCodeCommentQueryPersonalizationService.class);
		bind(CommitQueryPersonalizationService.class).to(DefaultCommitQueryPersonalizationService.class);
		bind(BuildQueryPersonalizationService.class).to(DefaultBuildQueryPersonalizationService.class);
		bind(PackQueryPersonalizationService.class).to(DefaultPackQueryPersonalizationService.class);
		bind(PullRequestAssignmentService.class).to(DefaultPullRequestAssignmentService.class);
		bind(SshKeyService.class).to(DefaultSshKeyService.class);
		bind(BuildMetricService.class).to(DefaultBuildMetricService.class);
		bind(ReferenceChangeService.class).to(DefaultReferenceChangeService.class);
		bind(GitLfsLockService.class).to(DefaultGitLfsLockService.class);
		bind(IssueScheduleService.class).to(DefaultIssueScheduleService.class);
		bind(LinkSpecService.class).to(DefaultLinkSpecService.class);
		bind(IssueLinkService.class).to(DefaultIssueLinkService.class);
		bind(IssueStateHistoryService.class).to(DefaultIssueStateHistoryService.class);
		bind(LinkAuthorizationService.class).to(DefaultLinkAuthorizationService.class);
		bind(EmailAddressService.class).to(DefaultEmailAddressService.class);
		bind(GpgKeyService.class).to(DefaultGpgKeyService.class);
		bind(IssueTextService.class).to(DefaultIssueTextService.class);
		bind(PullRequestTextService.class).to(DefaultPullRequestTextService.class);
		bind(CodeCommentTextService.class).to(DefaultCodeCommentTextService.class);
		bind(PendingSuggestionApplyService.class).to(DefaultPendingSuggestionApplyService.class);
		bind(IssueAuthorizationService.class).to(DefaultIssueAuthorizationService.class);
		bind(DashboardService.class).to(DefaultDashboardService.class);
		bind(DashboardUserShareService.class).to(DefaultDashboardUserShareService.class);
		bind(DashboardGroupShareService.class).to(DefaultDashboardGroupShareService.class);
		bind(DashboardVisitService.class).to(DefaultDashboardVisitService.class);
		bind(LabelSpecService.class).to(DefaultLabelSpecService.class);
		bind(ProjectLabelService.class).to(DefaultProjectLabelService.class);
		bind(BuildLabelService.class).to(DefaultBuildLabelService.class);
		bind(PackLabelService.class).to(DefaultPackLabelService.class);
		bind(PullRequestLabelService.class).to(DefaultPullRequestLabelService.class);
		bind(IssueTouchService.class).to(DefaultIssueTouchService.class);
		bind(PullRequestTouchService.class).to(DefaultPullRequestTouchService.class);
		bind(CodeCommentTouchService.class).to(DefaultCodeCommentTouchService.class);
		bind(AlertService.class).to(DefaultAlertService.class);
		bind(UpdateCheckService.class).to(DefaultUpdateCheckService.class);
		bind(StopwatchService.class).to(DefaultStopwatchService.class);
		bind(PackService.class).to(DefaultPackService.class);
		bind(PackBlobService.class).to(DefaultPackBlobService.class);
		bind(PackBlobReferenceService.class).to(DefaultPackBlobReferenceService.class);
		bind(AccessTokenAuthorizationService.class).to(DefaultAccessTokenAuthorizationService.class);
		bind(ReviewedDiffService.class).to(DefaultReviewedDiffService.class);
		bind(OAuthTokenService.class).to(DefaultOAuthTokenService.class);
		bind(IssueReactionService.class).to(DefaultIssueReactionService.class);
		bind(IssueCommentReactionService.class).to(DefaultIssueCommentReactionService.class);
		bind(PullRequestReactionService.class).to(DefaultPullRequestReactionService.class);
		bind(PullRequestCommentReactionService.class).to(DefaultPullRequestCommentReactionService.class);
		bind(IssueCommentRevisionService.class).to(DefaultIssueCommentRevisionService.class);
		bind(PullRequestCommentRevisionService.class).to(DefaultPullRequestCommentRevisionService.class);
		bind(IssueDescriptionRevisionService.class).to(DefaultIssueDescriptionRevisionService.class);
		bind(PullRequestDescriptionRevisionService.class).to(DefaultPullRequestDescriptionRevisionService.class);
		bind(SsoProviderService.class).to(DefaultSsoProviderService.class);
		bind(SsoAccountService.class).to(DefaultSsoAccountService.class);
		bind(BaseAuthorizationService.class).to(DefaultBaseAuthorizationService.class);
		
		bind(WebHookManager.class);
		
		contribute(CodePullAuthorizationSource.class, DefaultJobService.class);
        
		bind(CodeIndexService.class).to(DefaultCodeIndexService.class);
		bind(CodeSearchService.class).to(DefaultCodeSearchService.class);

		Bootstrap.executorService = new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
				new SynchronousQueue<>()) {

			@Override
			public void execute(Runnable command) {
				try {
					super.execute(SecurityUtils.inheritSubject(command));
				} catch (RejectedExecutionException e) {
					if (!isShutdown())
						throw ExceptionUtils.unchecked(e);
				}
			}

        };

	    bind(ExecutorService.class).toProvider(() -> Bootstrap.executorService).in(Singleton.class);
	    
	    bind(OsInfo.class).toProvider(() -> ExecutorUtils.getOsInfo()).in(Singleton.class);
	    
	    contributeFromPackage(LogInstruction.class, LogInstruction.class);	    
	    
		contribute(CodeProblemContribution.class, (build, blobPath, reportName) -> newArrayList());
	    
		contribute(LineCoverageContribution.class, (build, blobPath, reportName) -> new HashMap<>());
		contribute(AdministrationSettingContribution.class, () -> new ArrayList<>());
		contribute(ProjectSettingContribution.class, () -> new ArrayList<>());
		contribute(GitPreReceiveChecker.class, (project, submitter, refName, oldObjectId, newObjectId) -> null);

		bind(PackFilter.class);
	}
	
	private void configureSecurity() {
		contributeFromPackage(Realm.class, GeneralAuthorizingRealm.class);

		bind(ShiroFilterConfiguration.class).to(DefaultShiroFilterConfiguration.class);
		bind(RememberMeManager.class).to(DefaultRememberMeManager.class);
		bind(WebSecurityManager.class).to(DefaultWebSecurityManager.class);
		bind(FilterChainResolver.class).to(DefaultFilterChainResolver.class);
		bind(BasicAuthenticationFilter.class);
		bind(BearerAuthenticationFilter.class);
		bind(PasswordService.class).to(DefaultPasswordService.class);
		bind(ShiroFilter.class);
		install(new ShiroAopModule());
        contribute(FilterChainConfigurator.class, filterChainManager -> {
			filterChainManager.createChain("/**/info/refs", "noSessionCreation, authcBasic, authcBearer");
			filterChainManager.createChain("/**/git-upload-pack", "noSessionCreation, authcBasic, authcBearer");
			filterChainManager.createChain("/**/git-receive-pack", "noSessionCreation, authcBasic, authcBearer");
		});
        contributeFromPackage(Authenticator.class, Authenticator.class);
		
		bind(SSLFactory.class).toProvider(() -> KubernetesHelper.buildSSLFactory(Bootstrap.getTrustCertsDir())).in(Singleton.class);
	}
	
	private void configureGit() {
		contribute(ObjectMapperConfigurator.class, GitObjectMapperConfigurator.class);
		bind(GitService.class).to(DefaultGitService.class);
		bind(GitLocation.class).toProvider(GitLocationProvider.class);
		bind(GitFilter.class);
		bind(GoGetFilter.class);
		bind(GitLfsFilter.class);
		bind(GitPreReceiveCallback.class);
		bind(GitPostReceiveCallback.class);
		bind(SignatureVerificationService.class).to(DefaultSignatureVerificationService.class);
		contribute(CommandCreator.class, SshCommandCreator.class);
		contributeFromPackage(SignatureVerifier.class, SignatureVerifier.class);
	}
	
	private void configureRestful() {
		bind(ResourceConfig.class).toProvider(ResourceConfigProvider.class).in(Singleton.class);
		bind(ServletContainer.class).to(DefaultServletContainer.class);
		
		contribute(FilterChainConfigurator.class, filterChainManager -> filterChainManager.createChain("/~api/**", "noSessionCreation, authcBasic, authcBearer"));
		contribute(JerseyConfigurator.class, resourceConfig -> resourceConfig.packages(ProjectResource.class.getPackage().getName()));
		contribute(JerseyConfigurator.class, resourceConfig -> resourceConfig.register(ClusterResource.class));
		contribute(JerseyConfigurator.class, resourceConfig -> resourceConfig.register(McpHelperResource.class));
		contribute(JerseyConfigurator.class, resourceConfig -> resourceConfig.register(BuildSpecSchemaResource.class));
	}

	private void configureWeb() {
		bind(WicketServlet.class).to(DefaultWicketServlet.class);
		bind(WicketFilter.class).to(DefaultWicketFilter.class);
		bind(EditSupportRegistry.class).to(DefaultEditSupportRegistry.class);
		bind(WebSocketService.class).to(DefaultWebSocketService.class);
		bind(SessionDataStoreFactory.class).to(DefaultSessionDataStoreFactory.class);

		contributeFromPackage(EditSupport.class, EditSupport.class);
		
		bind(org.apache.wicket.protocol.http.WebApplication.class).to(WebApplication.class);
		bind(Application.class).to(WebApplication.class);
		bind(AvatarService.class).to(DefaultAvatarService.class);
		bind(WebSocketService.class).to(DefaultWebSocketService.class);
		
		contributeFromPackage(EditSupport.class, EditSupportLocator.class);
				
		bind(CommitIndexedBroadcaster.class);
		
		contributeFromPackage(DiffRenderer.class, DiffRenderer.class);
		contributeFromPackage(BlobRenderer.class, BlobRenderer.class);

		contribute(Extension.class, new EmojiExtension());
		contribute(Extension.class, new SourcePositionTrackExtension());
		
		contributeFromPackage(HtmlProcessor.class, HtmlProcessor.class);

		contribute(ResourcePackScopeContribution.class, () -> newArrayList(WebApplication.class));
		
		contributeFromPackage(ExceptionHandler.class, ExceptionHandler.class);
		contributeFromPackage(ExceptionHandler.class, ConstraintViolationExceptionHandler.class);
		contributeFromPackage(ExceptionHandler.class, PageExpiredExceptionHandler.class);
		contributeFromPackage(ExceptionHandler.class, WebApplicationExceptionHandler.class);
		
		bind(UrlService.class).to(DefaultUrlService.class);
		bind(CodeCommentEventBroadcaster.class);
		bind(PullRequestEventBroadcaster.class);
		bind(IssueEventBroadcaster.class);
		bind(BuildEventBroadcaster.class);
		bind(AlertEventBroadcaster.class);
		bind(UploadService.class).to(DefaultUploadService.class);
		
		bind(TaskButton.TaskFutureManager.class);
	}
	
	private void configureBuild() {
		bind(ResourceAllocator.class).to(DefaultResourceAllocator.class);
		bind(AgentService.class).to(DefaultAgentService.class);
		bind(AgentTokenService.class).to(DefaultAgentTokenService.class);
		bind(AgentAttributeService.class).to(DefaultAgentAttributeService.class);
		bind(AgentLastUsedDateService.class).to(DefaultAgentLastUsedDateService.class);
		
		contribute(ScriptContribution.class, new ScriptContribution() {

			@Override
			public GroovyScript getScript() {
				GroovyScript script = new GroovyScript();
				script.setName("determine-build-failure-investigator");
				script.setContent(newArrayList("io.cheeta.server.util.ScriptContribution.determineBuildFailureInvestigator()"));
				return script;
			}
			
		});
		contribute(ScriptContribution.class, new ScriptContribution() {

			@Override
			public GroovyScript getScript() {
				GroovyScript script = new GroovyScript();
				script.setName("get-build-number");
				script.setContent(newArrayList("io.cheeta.server.util.ScriptContribution.getBuildNumber()"));
				return script;
			}
			
		});
		contribute(ScriptContribution.class, new ScriptContribution() {

			@Override
			public GroovyScript getScript() {
				GroovyScript script = new GroovyScript();
				script.setName("get-current-user");
				script.setContent(newArrayList("io.cheeta.server.util.ScriptContribution.getCurrentUser()"));
				return script;
			}

		});
	}
	
	private void configurePersistence() {
		bind(DataService.class).to(DefaultDataService.class);
		
		bind(Session.class).toProvider(SessionProvider.class);
		bind(EntityManager.class).toProvider(SessionProvider.class);
		bind(SessionFactory.class).toProvider(SessionFactoryProvider.class);
		bind(EntityManagerFactory.class).toProvider(SessionFactoryProvider.class);
		bind(SessionFactoryService.class).to(DefaultSessionFactoryService.class);
		
	    contribute(ObjectMapperConfigurator.class, HibernateObjectMapperConfigurator.class);
	    
		bind(Interceptor.class).to(HibernateInterceptor.class);
		bind(PhysicalNamingStrategy.class).toInstance(new PrefixedNamingStrategy("o_"));
		
		bind(SessionService.class).to(DefaultSessionService.class);
		bind(TransactionService.class).to(DefaultTransactionService.class);
		bind(IdService.class).to(DefaultIdService.class);
		bind(Dao.class).to(DefaultDao.class);
		
	    TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
	    requestInjection(transactionInterceptor);
	    
	    bindInterceptor(Matchers.any(), new AbstractMatcher<AnnotatedElement>() {

			@Override
			public boolean matches(AnnotatedElement element) {
				return element.isAnnotationPresent(Transactional.class) && !((Method) element).isSynthetic();
			}
	    	
	    }, transactionInterceptor);
	    
	    SessionInterceptor sessionInterceptor = new SessionInterceptor();
	    requestInjection(sessionInterceptor);
	    
	    bindInterceptor(Matchers.any(), new AbstractMatcher<AnnotatedElement>() {

			@Override
			public boolean matches(AnnotatedElement element) {
				return element.isAnnotationPresent(Sessional.class) && !((Method) element).isSynthetic();
			}
	    	
	    }, sessionInterceptor);
	    
	    contribute(PersistListener.class, new PersistListener() {
			
			@Override
			public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
					throws CallbackException {
				return false;
			}
			
			@Override
			public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
					throws CallbackException {
				return false;
			}
			
			@Override
			public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState,
					String[] propertyNames, Type[] types) throws CallbackException {
				return false;
			}
			
			@Override
			public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
					throws CallbackException {
			}

		});
	    
		bind(XStream.class).toProvider(new com.google.inject.Provider<XStream>() {

			@SuppressWarnings("rawtypes")
			@Override
			public XStream get() {
				ReflectionProvider reflectionProvider = JVM.newReflectionProvider();
				XStream xstream = new XStream(reflectionProvider) {

					@Override
					protected MapperWrapper wrapMapper(MapperWrapper next) {
						return new MapperWrapper(next) {
							
							@Override
							public boolean shouldSerializeMember(Class definedIn, String fieldName) {
								Field field = reflectionProvider.getField(definedIn, fieldName);
								
								return field.getAnnotation(XStreamOmitField.class) == null 
										&& field.getAnnotation(Transient.class) == null 
										&& field.getAnnotation(OneToMany.class) == null 
										&& (field.getAnnotation(OneToOne.class) == null || field.getAnnotation(JoinColumn.class) != null)  
										&& field.getAnnotation(Version.class) == null;
							}
							
							@Override
							public String serializedClass(Class type) {
								if (type == null)
									return super.serializedClass(type);
								else if (type == PersistentBag.class)
									return super.serializedClass(ArrayList.class);
								else if (type.getName().contains("$HibernateProxy$"))
									return StringUtils.substringBefore(type.getName(), "$HibernateProxy$");
								else
									return super.serializedClass(type);
							}
							
						};
					}
					
				};
				xstream.allowTypesByWildcard(new String[] {"**"});				
				
				// register NullConverter as highest; otherwise NPE when unmarshal a map 
				// containing an entry with value set to null.
				xstream.registerConverter(new NullConverter(), XStream.PRIORITY_VERY_HIGH);
				xstream.registerConverter(new StringConverter(), XStream.PRIORITY_VERY_HIGH);
				xstream.registerConverter(new VersionedDocumentConverter(), XStream.PRIORITY_VERY_HIGH);
				xstream.registerConverter(new HibernateProxyConverter(), XStream.PRIORITY_VERY_HIGH);
				xstream.registerConverter(new CollectionConverter(xstream.getMapper()), XStream.PRIORITY_VERY_HIGH);
				xstream.registerConverter(new MapConverter(xstream.getMapper()), XStream.PRIORITY_VERY_HIGH);
				xstream.registerConverter(new ObjectMapperConverter(), XStream.PRIORITY_VERY_HIGH);
				xstream.registerConverter(new ISO8601DateConverter(), XStream.PRIORITY_VERY_HIGH);
				xstream.registerConverter(new ISO8601SqlTimestampConverter(), XStream.PRIORITY_VERY_HIGH); 
				xstream.registerConverter(new ReflectionConverter(xstream.getMapper(), xstream.getReflectionProvider()), 
						XStream.PRIORITY_VERY_LOW);
				xstream.autodetectAnnotations(true);
				return xstream;
			}
			
		}).in(Singleton.class);
	}
	
	@Override
	protected Class<? extends AbstractPlugin> getPluginClass() {
		if (Bootstrap.command != null) {
			if (RestoreDatabase.COMMAND.equals(Bootstrap.command.getName()))
				return RestoreDatabase.class;
			else if (ApplyDatabaseConstraints.COMMAND.equals(Bootstrap.command.getName()))
				return ApplyDatabaseConstraints.class;
			else if (BackupDatabase.COMMAND.equals(Bootstrap.command.getName()))
				return BackupDatabase.class;
			else if (CheckDataVersion.COMMAND.equals(Bootstrap.command.getName()))
				return CheckDataVersion.class;
			else if (Upgrade.COMMAND.equals(Bootstrap.command.getName()))
				return Upgrade.class;
			else if (CleanDatabase.COMMAND.equals(Bootstrap.command.getName()))
				return CleanDatabase.class;
			else if (ResetAdminPassword.COMMAND.equals(Bootstrap.command.getName()))
				return ResetAdminPassword.class;
			else if (Translate.COMMAND.equals(Bootstrap.command.getName()))
				return Translate.class;
			else
				throw new RuntimeException("Unrecognized command: " + Bootstrap.command.getName());
		} else {
			return Cheeta.class;
		}		
	}

}
