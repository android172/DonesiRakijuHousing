using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using WebAPI.Models;
using System.IO;

namespace WebAPI.Services
{
    public class FileService
    {
        private string path;
        private string[] folders;

        public FileService(string _path, string[] _folders)
        {
            path = _path;
            folders = _folders;

            Directory.CreateDirectory(path);
            foreach(var folder in folders)
            {
                Directory.CreateDirectory($"{path}\\{folder}");
            }
        }

        public FileData ReadFile(string folder, string fileName = null)
        {
            FileData fd = new FileData();
            if(fileName == null)
            {
                fileName = Directory.GetFiles($"{path}\\{folder}").FirstOrDefault();
            }
            FileInfo fi = new FileInfo(fileName);
            fd.Name = fi.Name;
            fd.Extension = fi.Extension;
            using (StreamReader stream = new StreamReader($"{path}\\{folder}\\{fi.Name}"))
            {
                fd.Content = stream.ReadToEnd();
            }
            return fd;
        }

        public void WriteFile(string folder, FileData file)
        {
            Directory.CreateDirectory($"{path}\\{folder}");
            using (StreamWriter stream = new StreamWriter($"{path}\\{folder}\\{file.Name}"))
            {
                stream.Write(file.Content);
            }
        }

        public void DeleteFile(string folder, string fileName = null)
        {
            if(fileName == null)
            {
                fileName = Directory.GetFiles($"{path}\\{folder}").FirstOrDefault();
            }
            File.Delete($"{path}\\{folder}\\{fileName}");
        }

        public void DeleteAllFiles(string folder)
        {
            var files = Directory.GetFiles($"{path}\\{folder}");
            foreach(var file in files)
            {
                File.Delete($"{path}\\{folder}\\{file}");
            }
        }

        public void WriteFolder(string folder, List<FileData> files)
        {
            Directory.CreateDirectory($"{path}\\{folder}");
            foreach(var file in files)
            {
                WriteFile(folder, file);
            }
        }

        public List<FileData> ReadFolder(string folder) 
        {
            List<FileData> files = new List<FileData>();

            foreach(string file in Directory.GetFiles($"{path}\\{folder}"))
            {
                FileData fd = new FileData();
                FileInfo fi = new FileInfo(file);
                fd.Extension = fi.Extension;
                fd.Name = fi.Name;

                fd.Content = "";
                using (StreamReader stream = new StreamReader(path))
                {
                    fd.Content = stream.ReadToEnd();
                }

                files.Add(fd);
            }
            return files;
        }
    }
}
