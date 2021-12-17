# Analysis of Health Effects of Natural Gas Flaring

This repo was developed in ![VS Code](data/images/vscode.jpeg) using Gradle for dependency management.

![Data Model](data/images/dataflow.png)

Remote-Sensing Data is obtained from 2 primary sources.
- Flaring event data is obtained from the VNF product produced by the Colorado School of Mines [EOG](https://payneinstitute.mines.edu/eog/)
- Weather data is obtained from Microsoft's [AzureML Opendatasets](https://docs.microsoft.com/en-us/python/api/azureml-opendatasets/azureml.opendatasets.noaagfsweather?view=azure-ml-py)

The data is ingested into a MongoDB database into a **VnfEvent** object and a **Gfs** object.  The code examples in this repo are in Kotlin, but MongoDB bindings for most languages are available.  Details of the data object structures are under the **src/main/kotline/dao** folder.

There are 2 example analyses.  **AppMaxExposure** shows an example of calculating methane equivalents for individual flaring events and using a simple Gaussian dispersion model to calculate worst case contaminant concentrations along the primary wind direction at a distance of 1km from the source.  **AppVnfGeoExample** shows an example of using country shapefile boundaries to assign flaring events by country.

Additional References
- [VNF Flaring Details](mdpi.com/1996-1073/9/1/14/htm)
- [Gaussian Plume Model](https://www.eng.uwo.ca/people/esavory/Gaussian%20plumes.pdf)
- [Emission/Combustion/Gas Composition Details](https://www.sciencedirect.com/science/article/pii/S1018363915000203)
- [Flaring Volume Data by Country](https://thedocs.worldbank.org/en/doc/1f7221545bf1b7c89b850dd85cb409b0-0400072021/original/WB-GGFR-Report-Design-05a.pdf)

# Working with the MongoDB
While users with remote-sensing expertise may wish to work with the raw VNF and GFS sources, downloading the *mongoDB.tar.gz* and working with the data objects is an easy way to get started exploring the flaring event data.  After installing MongoDB and downloading the tar.gz file, you do the following to create the *flare* db

<pre><code>
$ tar -xf *mongoDB.tar.gz
$ mongorestore dump/
</pre></code>
