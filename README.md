# backApp
#### ~ a simple Java-based utility that backs up files/folders at regular intervals

## Build
Requires maven.

Depends on 
- lombok
- jackson-databind
- apache-commons
- maven-jar- and maven-shade- plugins for build

In the project root, run
```shell
mvn package
```
Built jar(s) can be found in the `target/` folder

## Usage
This application is config based. There are 2 major types of configurations: **core** and **backup** configs.

### Core Config
The **core** config describes the overall configuration of the application and expects the following fields in the form of a JSON file, located at `$BACKUP_DAEMON_CONFIG`, or `~/.config/backApp/core-config.json` by default.

The file contains the following fields, but comes out of the box with a few defaults for each of them:

| Field                                | Type/Format                                           | Default Value                                                                     |
|--------------------------------------|-------------------------------------------------------|-----------------------------------------------------------------------------------|
| backupConfigFileLocation             | Absolute folder path                                  | ~/.config/backApp/bConf/                                                          |
| useSysTempAsFallback (unimplemented) | Boolean (true/false)                                  | true                                                                              |
| logging                              | {<br>String logLevel, <br>String logFileLocation<br>} | {<br>logLevel: "ERROR", <br>logFileLocation: "~/backApp-Backups/backApp.log"<br>} |
| maxThreads (backup thread pool size) | natural number                                        | 5                                                                                 |


### Backup Config
The **backup** config for a single backup task describes the characteristics of how the particular backup needs to be performed. Each backup task can be defined by the following fields in a single JSON file located at `coreConfig.backupConfigFileLocation`. File name does not matter, as backup names are specified in the file itself.

The following are the expected fields that can be found in a backup config JSON file:

| Field        | Type/Format                                                                                             | Required (default value, if not defined) |
|--------------|---------------------------------------------------------------------------------------------------------|------------------------------------------|
| name         | String                                                                                                  | **YES** (NA)                             |
| fromLocation | Absolute path to file/folder to backup                                                                  | **YES** (NA)                             |
| toLocation   | Absolute path of backup folder                                                                          | **NO** (~/backApp-Backups/backups/)	     |
| compression  | Enum Compression (See Compression Types below)                                                          | **NO** (`NONE`)                          |
| maxRetention | Natural number > 1                                                                                      | **NO** (5)                               |
| interval     | {<br>months: Whole number,<br>days: Whole number,<br>hours: Whole number,<br>minutes: Whole number<br>} | 	**NO** (1 day)                          |
| maxRetention | Natural number > 1                                                                                      | **NO** (5)                               |

The `lastTime` field (Format: `yyyy-MM-dd HH:mm`) is automatically set by the utility itself during its shutdown, as a mechanism to persist the last time at which this task was run. Could be manipulated to ru the task immediately.

#### Compression Types
Currently, there are 3 options available:
- `ZIP`: The folder/file is compressed after it is backed up into a `.zip` archive.
- `GZIP`: The folder/file is compressed after it is backed up into a `.tar.gz` archive.
- `NONE`: Default option, file/folder is left untouched after backup, might consume space for files that can be compressed.

### Notes
The backed up folder/file names are of the format: 
<br>
`<BACKUP_DIR>/<file/folder name>-<ddMMyyyy-hhmmss>.bkp[.archive]`


