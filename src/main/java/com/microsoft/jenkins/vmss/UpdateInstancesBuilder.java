package com.microsoft.jenkins.vmss;

import com.microsoft.azure.management.Azure;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Item;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.util.ListBoxModel;
import org.kohsuke.stapler.AncestorInPath;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class UpdateInstancesBuilder extends BaseBuilder {

    private final String instanceIds;

    @DataBoundConstructor
    public UpdateInstancesBuilder(
            final String azureCredentialsId,
            final String resourceGroup,
            final String name,
            final String instanceIds) {
        super(azureCredentialsId, resourceGroup, name);
        this.instanceIds = instanceIds;
    }

    public String getInstanceIds() {
        return instanceIds;
    }

    @Override
    public void perform(@Nonnull final Run<?, ?> run,
                        @Nonnull final FilePath workspace,
                        @Nonnull final Launcher launcher,
                        @Nonnull final TaskListener listener) throws InterruptedException, IOException {

        listener.getLogger().println(Messages.UpdateInstancesBuilder_PerformLogStart());

        listener.getLogger().println(Messages.UpdateInstancesBuilder_PerformLogInstanceIDs(instanceIds.toString()));

        final Azure azure = getAzureClient();

        azure.virtualMachineScaleSets().inner().updateInstances(
                getResourceGroup(), getName(), parseInstanceIds(instanceIds));

        listener.getLogger().println(Messages.UpdateInstancesBuilder_PerformLogSuccess());
    }

    static List<String> parseInstanceIds(final String instanceIdsText) {
        return Arrays.asList(instanceIdsText.split(","));
    }

    @Extension
    public static class DescriptorImpl extends BaseBuilder.DescriptorImpl {

        @Override
        public String getDisplayName() {
            return Messages.UpdateInstancesBuilder_DisplayName();
        }

        public ListBoxModel doFillAzureCredentialsIdItems(@AncestorInPath final Item owner) {
            return listAzureCredentialsIdItems(owner);
        }

        public ListBoxModel doFillResourceGroupItems(@QueryParameter final String azureCredentialsId) {
            return listResourceGroupItems(azureCredentialsId);
        }

        public ListBoxModel doFillNameItems(@QueryParameter final String azureCredentialsId,
                                            @QueryParameter final String resourceGroup) {
            return listVMSSItems(azureCredentialsId, resourceGroup);
        }
    }
}