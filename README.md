# Geeks for Geeks content parser

A spring-boot based stand alone application that generates minimal bootstrap pages of geeksforgeeks pages. **This is strictly for informational/educational purpose and not for any other benifits**.

### How to Install
--
The installation process is fairly simple. This requires a working internet connection, java 1.8, maven and git. The instructions for the same are as follows :

```bash
$ git clone https://github.com/gaurs/gfg-parser.git gfg
$ cd gfg
$ maven clean install
```      
### Customization
--
The `start.path` property in the `application.properties` can be modified accordingly to update the starting location for the parsing process. Also `output.dir` denotes the location of generated files which can be modified as deemed appropriate.

### Output
--
The output contains minimal bootstrap based pages with an index page that lists the conents of the download directory. All efforts are based on the assumption of curremt tags used in the gfg portal. As this is a scrapping tool and not any API based tool, any change in the page structure at gfg requires corresponding changes in the utility code.
