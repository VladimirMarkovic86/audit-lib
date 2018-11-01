(ns audit-lib.core
  (:require [mongo-lib.core :as mon]
            [utils-lib.core :as utils]
            [session-lib.core :as ssn]))

(defn audit
  "Make trace of users action"
  [request
   response]
  (when (System/getenv "AUDIT_ACTIONS")
    (let [cookie-string (:cookie request)
          session-uuid (ssn/get-cookie
                         cookie-string
                         :long-session)
          [session-uuid
           session-collection] (if-not session-uuid
                                 [(ssn/get-cookie
                                    cookie-string
                                    :session)
                                  "session"]
                                 [session-uuid
                                  "long-session"])]
      (when-let [session-uuid session-uuid]
        (when-let [session-obj (mon/mongodb-find-one
                                 session-collection
                                 {:uuid session-uuid})]
          (let [user-id (:user-id session-obj)]
            (when-let [user (mon/mongodb-find-by-id
                              "user"
                              user-id)]
              (let [body (or (:body
                               request)
                             "")
                    body (str
                           body)
                    body-length (if (< 300
                                       (count
                                         body))
                                  300
                                  (count
                                    body))
                    body (.substring
                           body
                           0
                           body-length)]
                (mon/mongodb-insert-one
                  "audit"
                  {:user-entity (dissoc
                                  user
                                  :password
                                  :roles)
                   :date (java.util.Date.)
                   :action (:request-uri
                             request)
                   :body body
                   :response (:status
                               response)})
               ))
           ))
       ))
   ))

