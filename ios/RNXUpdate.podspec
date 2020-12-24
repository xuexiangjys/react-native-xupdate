
Pod::Spec.new do |s|
  s.name         = "RNXUpdate"
  s.version      = "1.0.2"
  s.summary      = "RNXUpdate"
  s.description  = <<-DESC
                  RNXUpdate
                   DESC
  s.homepage     = "https://github.com/react-native-xupdate"
  s.license      = { :type => "Apache-2.0", :file => "LICENSE" }
  s.author             = { "xuexiangjys" => "xuexiangjys@163.com" }
  s.platform     = :ios, "9.0"
  s.source       = { :git => "https://github.com/react-native-xupdate.git", :tag => "master" }
  s.source_files  = "RNXUpdate/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

