package org
import future.keywords.in

policy_name["use_official_docker_image"]

use_official_docker_image[image] = reason {
  some image in docker_images   # docker_images are parsed below
  not startswith(image, "cimg")
  reason := sprintf("%s is not an approved Docker image", [image])
}

# helper to parse docker images from the config
docker_images := {image | walk(input, [path, value])  
# walk the entire config tree
                          path[_] == "docker"         
# find any settings that match 'docker'
                          image := value[_].image}    
# grab the images from that section

hard_fail["use_official_docker_image"]

# enable_rule["use_official_docker_image"]