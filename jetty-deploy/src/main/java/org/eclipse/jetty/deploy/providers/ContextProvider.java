package org.eclipse.jetty.deploy.providers;

import java.io.File;
import java.io.FilenameFilter;

import org.eclipse.jetty.deploy.App;
import org.eclipse.jetty.deploy.ConfigurationManager;
import org.eclipse.jetty.deploy.util.FileID;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.xml.XmlConfiguration;

/** Context directory App Provider.
 * <p>This specialization of {@link ScanningAppProvider} is the
 * replacement for the old (and deprecated) <code>org.eclipse.jetty.deploy.ContextDeployer</code> and it will scan a directory
 * only for context.xml files.
 */
public class ContextProvider extends ScanningAppProvider
{
    private ConfigurationManager _configurationManager;

    public ContextProvider()
    {
        super(  new FilenameFilter()
        {
            public boolean accept(File dir, String name)
            {
                if (!dir.exists())
                    return false;
                String lowername = name.toLowerCase();
                return  (lowername.endsWith(".xml") && !new File(dir,name).isDirectory());
            }
        });
    }


    /* ------------------------------------------------------------ */
    public ConfigurationManager getConfigurationManager()
    {
        return _configurationManager;
    }
    
    /* ------------------------------------------------------------ */
    /** Set the configurationManager.
     * @param configurationManager the configurationManager to set
     */
    public void setConfigurationManager(ConfigurationManager configurationManager)
    {
        _configurationManager = configurationManager;
    }

    /* ------------------------------------------------------------ */
    public ContextHandler createContextHandler(App app) throws Exception
    {
        Resource resource = Resource.newResource(app.getOriginId());
        File file = resource.getFile();
        
        if (resource.exists() && FileID.isXmlFile(file))
        {
            XmlConfiguration xmlc = new XmlConfiguration(resource.getURL());
            
            xmlc.getIdMap().put("Server",getDeploymentManager().getServer());
            if (getConfigurationManager() != null)
                xmlc.getProperties().putAll(getConfigurationManager().getProperties());
            return (ContextHandler)xmlc.configure();
        }
        
        throw new IllegalStateException("App resouce does not exist "+resource);
    }
    
}
