package org.infinispan.configuration.cache;

import org.infinispan.commons.CacheConfigurationException;
import org.infinispan.commons.configuration.Builder;
import org.infinispan.configuration.global.GlobalConfiguration;

import java.util.Arrays;

/**
 * Defines clustered characteristics of the cache.
 *
 * @author pmuir
 *
 */
public class ClusteringConfigurationBuilder extends AbstractConfigurationChildBuilder implements
      ClusteringConfigurationChildBuilder, Builder<ClusteringConfiguration> {

   private CacheMode cacheMode = CacheMode.LOCAL;
   private final AsyncConfigurationBuilder asyncConfigurationBuilder;
   private final HashConfigurationBuilder hashConfigurationBuilder;
   private final L1ConfigurationBuilder l1ConfigurationBuilder;
   private final StateTransferConfigurationBuilder stateTransferConfigurationBuilder;
   private final SyncConfigurationBuilder syncConfigurationBuilder;
   private final PartitionHandlingConfigurationBuilder partitionHandlingConfigurationBuilder;

   ClusteringConfigurationBuilder(ConfigurationBuilder builder) {
      super(builder);
      this.asyncConfigurationBuilder = new AsyncConfigurationBuilder(this);
      this.hashConfigurationBuilder = new HashConfigurationBuilder(this);
      this.l1ConfigurationBuilder = new L1ConfigurationBuilder(this);
      this.stateTransferConfigurationBuilder = new StateTransferConfigurationBuilder(this);
      this.syncConfigurationBuilder = new SyncConfigurationBuilder(this);
      this.partitionHandlingConfigurationBuilder = new PartitionHandlingConfigurationBuilder(this);
   }

   /**
    * Cache mode. See {@link CacheMode} for information on the various cache modes available.
    */
   public ClusteringConfigurationBuilder cacheMode(CacheMode cacheMode) {
      this.cacheMode = cacheMode;
      return this;
   }

   public CacheMode cacheMode() {
      return cacheMode;
   }

   /**
    * Configure async sub element. Once this method is invoked users cannot subsequently invoke
    * <code>configureSync()</code> as two are mutually exclusive
    */
   @Override
   public AsyncConfigurationBuilder async() {
      if (cacheMode.isSynchronous())
         throw new IllegalStateException("Cannot configure a async for an sync cache. Set the cache mode to async first.");
      return asyncConfigurationBuilder;
   }

   /**
    * Configure hash sub element
    */
   @Override
   public HashConfigurationBuilder hash() {
      return hashConfigurationBuilder;
   }

   /**
    * This method allows configuration of the L1 cache for distributed
    * caches. When this method is called, it automatically enables L1. So,
    * if you want it to be disabled, make sure you call
    * {@link org.infinispan.configuration.cache.L1ConfigurationBuilder#disable()}
    */
   @Override
   public L1ConfigurationBuilder l1() {
      return l1ConfigurationBuilder;
   }

   /**
    * Configure the {@code stateTransfer} sub element for distributed and replicated caches.
    * It doesn't have any effect on LOCAL or INVALIDATION-mode caches.
    */
   @Override
   public StateTransferConfigurationBuilder stateTransfer() {
      return stateTransferConfigurationBuilder;
   }

   @Override
   public SyncConfigurationBuilder sync() {
      if (!cacheMode.isSynchronous())
         throw new IllegalStateException("Cannot configure a sync for an async cache. Set the cache mode to sync first.");
      return syncConfigurationBuilder;
   }

   @Override
   public PartitionHandlingConfigurationBuilder partitionHandling() {
      return partitionHandlingConfigurationBuilder;
   }

   @Override
   public
   void validate() {
      for (Builder<?> validatable:
            Arrays.asList(asyncConfigurationBuilder, hashConfigurationBuilder, l1ConfigurationBuilder,
                          syncConfigurationBuilder, stateTransferConfigurationBuilder, partitionHandlingConfigurationBuilder)) {
         validatable.validate();
      }
   }

   @Override
   public void validate(GlobalConfiguration globalConfig) {
      if (cacheMode().isClustered() && globalConfig.transport().transport() == null) {
         throw new CacheConfigurationException("Must have a transport set in the global configuration in " +
               "order to define a clustered cache");
      }

      for (ConfigurationChildBuilder validatable:
         Arrays.asList(asyncConfigurationBuilder, hashConfigurationBuilder, l1ConfigurationBuilder,
                       syncConfigurationBuilder, stateTransferConfigurationBuilder, partitionHandlingConfigurationBuilder)) {
         validatable.validate(globalConfig);
      }
   }

   @Override
   public
   ClusteringConfiguration create() {
      return new ClusteringConfiguration(cacheMode, asyncConfigurationBuilder.create(), hashConfigurationBuilder.create(),
            l1ConfigurationBuilder.create(), stateTransferConfigurationBuilder.create(), syncConfigurationBuilder.create(), partitionHandlingConfigurationBuilder.create());
   }

   @Override
   public ClusteringConfigurationBuilder read(ClusteringConfiguration template) {
      this.cacheMode = template.cacheMode();
      asyncConfigurationBuilder.read(template.async());
      hashConfigurationBuilder.read(template.hash());
      l1ConfigurationBuilder.read(template.l1());
      stateTransferConfigurationBuilder.read(template.stateTransfer());
      syncConfigurationBuilder.read(template.sync());
      partitionHandlingConfigurationBuilder.read(template.partitionHandling());

      return this;
   }

   @Override
   public String toString() {
      return "ClusteringConfigurationBuilder{" +
            "async=" + asyncConfigurationBuilder +
            ", cacheMode=" + cacheMode +
            ", hash=" + hashConfigurationBuilder +
            ", l1=" + l1ConfigurationBuilder +
            ", stateTransfer=" + stateTransferConfigurationBuilder +
            ", sync=" + syncConfigurationBuilder +
            '}';
   }

}
