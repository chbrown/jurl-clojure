## jurl

[![Travis CI Build Status](https://travis-ci.org/chbrown/jurl-clojure.svg?branch=master)](https://travis-ci.org/chbrown/jurl-clojure)
[![Coverage Status](https://coveralls.io/repos/github/chbrown/jurl-clojure/badge.svg?branch=master)](https://coveralls.io/github/chbrown/jurl-clojure?branch=master)
[![Clojars Project](https://img.shields.io/clojars/v/jurl.svg)](https://clojars.org/jurl)

Cross-platform URL parsing and manipulation.

Uses reader conditionals to support Clojure and ClojureScript, and thus requires Clojure 1.7.0 or newer.


## Example

```clojure
(ns user
  (:require [jurl.core :as jurl]))
```

URL-encoding and -decoding:

```clojure
=> (jurl/encode "☺hai?")
"%E2%98%BAhai%3F"

=> (jurl/decode "http%3A%2F%2F%F0%9F%90%B4-e%F0%9F%93%9A.com")
"http://🐴-e📚.com"
```

Querystring parsing (use with `js/window.location.search` in ClojureScript):

```clojure
=> (jurl/search->seq "?uid=1&uid=5001")
(["uid" "1"] ["uid" "5001"])

=> (jurl/search->map "?uid=1&uid=5001&date=20170611")
{"uid" ["1" "5001"], "date" ["20170611"]}
```


## License

Copyright © 2017 Christopher Brown. [Eclipse Public License - v 1.0](https://www.eclipse.org/legal/epl-v10.html).
