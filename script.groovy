import groovy.json.JsonSlurperClassic

def images_lsit() {

// get new auth token from Google Cloud for this session
def auth_token = "gcloud auth print-access-token".execute().text.replaceAll("\r\n", "")

// get specific image tags as JSON with all information about each tag, including creation timestamp
def url = "curl -s -u _token:${auth_token} https://gcr.io/v2/pet-clinic-331616/spring-petclinic/tags/list"
def gcr_tags_json = url.execute().text.replaceAll("\r\n", "")

// unwrap JSON to Groovy object
def data = new JsonSlurperClassic().parseText(gcr_tags_json)
// prepare an empty hash map to store sorted images later, the "key" will be a timestamp
def tags_by_date = [:]

// let's sort the images hash map by creation date?
def timestamps = data.manifest.collect{ it.value.timeCreatedMs }.sort()
data.manifest.each{ tags_by_date[it.value.timeCreatedMs] = it.value.tag[0] }


// remember we always must return a List in order for Jenkins to pick up the result
def sorted_tags = []
// simply put the image names into a List, but now it will be in strict order by timestamp (keys)
for(timestamp in timestamps){
    sorted_tags.push(tags_by_date[timestamp])
}
return sorted_tags

}

return this