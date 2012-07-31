(ns soundcljs.queue)

;; FIFO polling queue
;; Functions of no arguments can be pushed onto the queue

(def ^:private max-operations 10)

(def ^:private operations (atom 0))

(def ^:private queue (atom []))

(def ^:private queue-interval
  "Interval at which the queue is polled."
  100)

(def ^:private paused (atom false))

(defn ^:private process-element? []
  "Determine whether work is awaiting in the queue and if the current
number of operations doesn't exceed `max-operations`."
  (and (not @paused)
       (pos? (count @queue))
       (< @operations max-operations)))

(defn ^:private pull []
  "Return an element from the queue or nil."
  (let [x (first @queue)]
    (swap! queue #(vec (rest %)))
    x))

(defn push [x]
  "Add an element to the queue."
  (swap! queue conj x))

(defn process-element [x]
  (do
    (swap! operations inc)
    (.log js/console (str "processing " x))
    (apply x nil)
    (swap! operations dec)))

(defn run []
  "Process elements from the queue or poll it for more work whenever
further elements shouldn't be processed."
  (if (process-element?)
    (do
      (process-element (pull))
      (recur))
    (do (.log js/console "Too many operations, timeout.")
        (.setTimeout js/window run queue-interval))))

(defn pause []
  (reset! paused true))

(defn resume []
  (reset! paused false))
