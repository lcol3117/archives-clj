(ns archives.core
  (:require [clojure.core.async
    :as a
    :refer [>! <! >!! <!! go chan buffer close! thread alts! alts!! timeout]]))

(defn archive [in-chan-args pass-chan-args flow-map-given]
  (do
    (def in-chan (apply chan in-chan-args))
    (def pass-chan (apply chan pass-chan-args))
    (go-loop [_ nil]
      ; run archive process
      (recur nil))
    {
      :in-chan in-chan
      :pass-chan pass-chan}))

(defn give!! [archive v]
  (>!! (archive :in-chan) {
                            :task :store
                            :data v}))

(defn retrieve!! [archive category]
  (do
    (def c-pass-chan (chan 1))
    (let [
           t (thread
            (>!! (archive :in-chan) {
                                      :task :locate
                                      :data category})
            (<!! (archive :out-chan)))]
      (<!! t))))
