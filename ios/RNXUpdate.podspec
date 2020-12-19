
Pod::Spec.new do |s|
  s.name         = "RNXUpdate"
  s.version      = "1.0.1"
  s.summary      = "RNXUpdate"
  s.description  = <<-DESC
                  RNXUpdate
                   DESC
  s.homepage     = ""
  s.license      = "MIT"
  # s.license      = { :type => "MIT", :file => "FILE_LICENSE" }
  s.author             = { "author" => "author@domain.cn" }
  s.platform     = :ios, "7.0"
  s.source       = { :git => "https://github.com/author/RNXUpdate.git", :tag => "master" }
  s.source_files  = "RNXUpdate/**/*.{h,m}"
  s.requires_arc = true


  s.dependency "React"
  #s.dependency "others"

end

