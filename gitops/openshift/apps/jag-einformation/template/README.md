## Templates to create openshift components related to jag-einformation api deployment

### Command to execute template
1) Login to OC using login command
2) Run below command in each env. namespace dev/test/prod
   ``oc process -f jag-einformation.yaml --param-file=jag-einformation.env | oc apply -f -``


