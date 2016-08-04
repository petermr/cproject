# SCRAPE workflow

## getpapers on Crossref

`getpapers` is run on Crossref, normally with a single day (from == until) and outputs results.json into a CProject.

## create doi/urlList for `quickscrape` (from Crossref metadata)

TODO
```
cmine --project <project> --urls <file> --metadata crossref
```
extract URLs to <file>


### shuffle URLs

TODO
```
cmine --project <project> --urls <file> --shuffle
```

URLs are shuffled so as to avoid hitting given publisher too frequently

### whitelist 

extract only urls/DOIs with domain prefix 
```
cmine --project <project> --urls <file> --whitelist <whiteFile>
```

### blacklist 

omit urls/DOIs with domain prefix 
```
cmine --project <project> --urls <file> --blacklist <blackFile>
```

## create metadata as CSV file

```
cmine --project <project> --metadata crossref --csv <csvfile> --cols author publisher doi ...
cmine --project <project> --metadata crossref --csv <csvfile> --colfile <colfile>
cmine --project <project> --metadata crossref --csv <csvfile> // creates default metadata (from file?)
```



