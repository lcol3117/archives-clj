(ns archives.core
  (:require [clojure.core.async
    :as a
    :refer [>! <! >!! <!! go chan thread]]))

(defn archive [in-chan-args pass-chan-args flow-map-given]
  (do
    (def in-chan (apply chan in-chan-args))
    (def pass-chan (apply chan pass-chan-args))
    (go-loop [curr-archive []]
      (def new-given (<! in-chan))
      (def novelty
        (if (= (new-given :task) :store)
          (new-given :data)))
      (if (= (new-given :task) :retrieve)
        (>! (new-given :data) curr-archive))
      (if novelty (recur (conj curr-archive novelty)) (recur curr-archive)))
    {
      :in-chan in-chan
      :pass-chan pass-chan}))

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
