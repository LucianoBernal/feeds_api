#!/bin/sh

ES_NODE=localhost
ES_SHARDS=1
ES_REPLICAS=1
ES_INDEX_NAME=crack

#creacion indice
curl -XPUT "http://$ES_NODE:9200/$ES_INDEX_NAME/" -d "{
    \"settings\": {
        \"number_of_shards\": \"$ES_SHARDS\",
	\"number_of_replicas\" : \"$ES_REPLICAS\"
	}
}"

#mapping
curl -X POST "http://$ES_NODE:9200/$ES_INDEX_NAME/_mapping/conversation_data" -d "{
     \"conversation_data\": {
       \"properties\": {
       \"application\": {
            \"type\": \"string\",
           \"index\":    \"not_analyzed\"
         },
         \"conversationId\": {
           \"type\": \"string\",
           \"index\":    \"not_analyzed\"
         },
         \"date\": {
           \"type\": \"long\"
         },
         \"deletedBy\": {
           \"type\": \"long\"
         },
         \"ignoredBy\": {
           \"type\": \"long\"
         },
         \"eventsData\": {
   \"type\": \"nested\",
           \"properties\": {
             \"key\": {
               \"type\": \"string\",
               \"index\":    \"not_analyzed\"
             },
             \"value\": {
               \"type\": \"string\",
               \"index\":    \"not_analyzed\"
             }
           }
         },
         \"format\": {
           \"type\": \"string\",
           \"index\":    \"not_analyzed\"
         },
         \"id\": {
            \"type\": \"string\",
           \"index\":    \"not_analyzed\"
         },
         \"key\": {
           \"type\": \"string\",
           \"index\":    \"not_analyzed\"
         },
         \"length\": {
           \"type\": \"long\"
         },
         \"receipts\": {
           \"type\": \"nested\",
           \"properties\": {
             \"date\": {
               \"type\": \"long\"
             },
             \"type\": {
               \"type\": \"string\",
               \"index\":    \"not_analyzed\"
             },
             \"user\": {
               \"type\": \"long\"
             }
           }
         },
         \"sender\": {
           \"type\": \"long\"
         },
         \"text\": {
           \"type\": \"string\"
         },
         \"thumbnail\": {
           \"type\": \"string\",
           \"index\":    \"not_analyzed\"
         },
         \"type\": {
           \"type\": \"string\",
           \"index\":    \"not_analyzed\"
         },
         \"url\": {
           \"type\": \"string\",
           \"index\":    \"not_analyzed\"
         },
         \"userId\": {
           \"type\": \"long\"
         }
       },
       \"_all\": { \"enabled\": false }
     }
   }"

curl -X POST "http://$ES_NODE:9200/$ES_INDEX_NAME/_mapping/user" -d "{
     \"user\":{
        \"properties\":{
           \"count\":{
             \"type\": \"long\"
           },
           \"conversations\":{
              \"type\":\"string\",
              \"index\":    \"not_analyzed\"
           }
        },
       \"_all\": { \"enabled\": false }
     }
    }
   }"

curl -X POST "http://$ES_NODE:9200/$ES_INDEX_NAME/_mapping/conversation" -d "{
   \"conversation\":{
      \"properties\":{
         \"deletedBy\":{
            \"properties\":{
               \"key\":{
                  \"type\":\"string\",
                  \"index\":\"not_analyzed\"
               },
               \"value\":{
                  \"type\":\"long\"
               }
            }
         },
         \"id\":{
            \"type\":\"string\",
            \"index\":\"not_analyzed\"
         },
         \"type\":{
            \"type\":\"string\",
            \"index\":\"not_analyzed\"
         },
         \"unreadMessages\":{
            \"properties\":{
               \"key\":{
                  \"type\":\"string\",
                  \"index\":\"not_analyzed\"
               },
               \"value\":{
                  \"type\":\"long\"
               }
            }
         },
         \"users\":{
            \"type\":\"long\"
         }
      },
      \"_all\": { \"enabled\": false }
   }
}"




