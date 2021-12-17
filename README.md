# Analysis of Health Effects of Natural Gas Flaring


![Data Model](data/images/dataflow.png)

Remote-Sensing Data is obtained from 2 primary sources.
- Flaring event data is obtained from the VNF product produced by the Colorado School of Mines [EOG](https://payneinstitute.mines.edu/eog/)
- Weather data is obtained from Microsoft's [AzureML Opendatasets](https://docs.microsoft.com/en-us/python/api/azureml-opendatasets/azureml.opendatasets.noaagfsweather?view=azure-ml-py)

The data is ingested into a MongoDB database into a **VnfEvent** object and a **Gfs** object.  The code examples in this repo are in Kotlin, but MongoDB bindings for most languages are available.  Details of the data object structures are under the **src/main/kotline/dao** folder.

There are 2 example analyses.  **AppMaxExposure** shows an example of calculating methane equivalents for individual flaring events and using a simple Gaussian dispersion model to calculate worst case contaminant concentrations along the primary wind direction at a distance of 1km from the source.  **AppVngGeoExample** shows an example of using country shapefile boundariues to assign flaring events by country.
