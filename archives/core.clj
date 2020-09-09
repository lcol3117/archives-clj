(ns archives.core
  (:require [clojure.core.async
    :as a
    :refer [>! <! >!! <!! go chan thread]]))

(defn archive
  ([]
    (archive [] []))
  ([chan-args]
    (archive chan-args chan-args))
  ([in-chan-args pass-chan-args]
    (do
      (def in-chan (apply chan in-chan-args))
      (def pass-chan (apply chan pass-chan-args))
      (a/go-loop [curr-archive []]
        (def new-given (<! in-chan))
        (def novelty
          (if (= (new-given :task) :store)
            (new-given :data)))
        (if (= (new-given :task) :retrieve)
          (>! (new-given :data) curr-archive))
        (if novelty (recur (conj curr-archive novelty)) (recur curr-archive)))
      {
        :in-chan in-chan
        :pass-chan pass-chan})))

(defn >>!! [archive v]
  (>!! (archive :in-chan) {
                            :task :store
                            :data v}))

(defn <<!! [archive category]
  (do
    (def c-pass-chan (chan 1))
    (let [
           t (thread
            (>!! (archive :in-chan) {
                                      :task :retrieve
                                      :data c-pass-chan})
            (<!! c-pass-chan))]
      (<!! t))))

(defn test-archive []
  (do
    (def my-archive (archive))
    (>>!! my-archive :first)
    (>>!! my-archive :second)
    (<<!! my-archive)
    (assert (= (<<!! my-archive) [:first :second]))))

(test-archive)
