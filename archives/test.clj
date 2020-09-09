(ns archives.test
  (:require archives.core :as archv
    :refer [>>!! <<!! archive]))

(def my-archive (archive [] []))

(>>!! my-archive :first)
(>>!! my-archive :second)

(<<!! my-archive)

(assert (= (<<!! my-archive) [:first :second]))
