package com.makelove.soAnalyze;

import com.makelove.soAnalyze.bean.ElfType32;
//    ELF头部(ELF_Header): 每个ELF文件都必须存在一个ELF_Header,
//    这里存放了很多重要的信息用来描述整个文件的组织,如: 版本信息,入口信息,
//    偏移信息等。程序执行也必须依靠其提供的信息。

//    程序头部表(Program_Header_Table): 可选的一个表，用于告诉系统如何在内存中创建映像,在图中也可以看出来,
//    有程序头部表才有段,有段就必须有程序头部表。其中存放各个段的基本信息(包括地址指针)。

//    节区头部表(Section_Header_Table): 类似与Program_Header_Table,但与其相对应的是节区(Section)。

//    节区(Section): 将文件分成一个个节区，每个节区都有其对应的功能，如符号表，哈希表等。

//    段(Segment): 嗯…就是将文件分成一段一段映射到内存中。段中通常包括一个或多个节区

//        动态符号表 (.dynsym) 用来保存与动态链接相关的导入导出符号
//        不包括模块内部的符号。而 .symtab 则保存所有符号，包括 .dynsym 中的符号。
//
//        动态符号表中所包含的符号的符号名保存在动态符号字符串表 .dynstr 中。
public class SoParse {


    private static ElfType32 type_32 = new ElfType32();

    /**
     * 解析Elf的头部信息
     *
     * @param header
     */
    public static void parseHeader(byte[] header, int offset) {
        if (header == null) {
            LogUtils.e("header is null");
            return;
        }
        /**
         *  public byte[] e_ident = new byte[16];
         public short e_type;
         public short e_machine;
         public int e_version;
         public int e_entry;
         public int Program_header_off;
         public int Section_header_off;
         public int e_flags;
         public short e_ehsize;
         public short e_phentsize;
         public short e_phnumCount;
         public short e_shentsize;
         public short e_shnumCount;
         public short e_shstrndx;
         */
        type_32.hdr.e_ident = Utils.copyBytes(header, 0, 16);//魔数

        type_32.hdr.e_type = Utils.copyBytes(header, 16, 2);

        type_32.hdr.e_machine = Utils.copyBytes(header, 18, 2);
        type_32.hdr.e_version = Utils.copyBytes(header, 20, 4);
        type_32.hdr.e_entry = Utils.copyBytes(header, 24, 4);
        type_32.hdr.Program_header_off = Utils.copyBytes(header, 28, 4);
        type_32.hdr.Section_header_off = Utils.copyBytes(header, 32, 4);
        type_32.hdr.e_flags = Utils.copyBytes(header, 36, 4);
        type_32.hdr.e_ehsize = Utils.copyBytes(header, 40, 2);
        type_32.hdr.e_phentsize = Utils.copyBytes(header, 42, 2);
        type_32.hdr.e_phnumCount = Utils.copyBytes(header, 44, 2);
        type_32.hdr.e_shentsize = Utils.copyBytes(header, 46, 2);
        type_32.hdr.e_shnumCount = Utils.copyBytes(header, 48, 2);
        type_32.hdr.e_shstrndx = Utils.copyBytes(header, 50, 2);
    }


    /**
     * 解析段头信息内容
     */
    public static void parseSectionHeaderList(byte[] header) {
        int offset = Utils.byte2Int(type_32.hdr.Section_header_off);

        int header_size = 40;//40个字节
        int header_count = Utils.byte2Short(type_32.hdr.e_shnumCount);//头部的个数
        byte[] des = new byte[header_size];

        for (int i = 0; i < header_count; i++) {
            System.arraycopy(header, i * header_size + offset, des, 0, header_size);

            type_32.SectionHeaderList.add(parseSectionHeader(des));
        }
    }


    /**
     *  public byte[] sh_name = new byte[4];
     public byte[] sh_type = new byte[4];
     public byte[] sh_flags = new byte[4];
     public byte[] sh_addr = new byte[4];
     public byte[] sh_offset = new byte[4];
     public byte[] sh_size = new byte[4];
     public byte[] sh_link = new byte[4];
     public byte[] sh_info = new byte[4];
     public byte[] sh_addralign = new byte[4];
     public byte[] sh_entsize = new byte[4];
     */
    private static ElfType32.Elf32_SectionHeaderItem parseSectionHeader(byte[] header) {
        ElfType32.Elf32_SectionHeaderItem shdr = new ElfType32.Elf32_SectionHeaderItem();

        shdr.sh_name = Utils.copyBytes(header, 0, 4);
        shdr.sh_type = Utils.copyBytes(header, 4, 4);
        shdr.sh_flags = Utils.copyBytes(header, 8, 4);
        shdr.sh_addr = Utils.copyBytes(header, 12, 4);
        shdr.sh_offset = Utils.copyBytes(header, 16, 4);
        shdr.sh_size = Utils.copyBytes(header, 20, 4);
        shdr.sh_link = Utils.copyBytes(header, 24, 4);
        shdr.sh_info = Utils.copyBytes(header, 28, 4);
        shdr.sh_addralign = Utils.copyBytes(header, 32, 4);
        shdr.sh_entsize = Utils.copyBytes(header, 36, 4);
        return shdr;
    }

    /**
     * 解析程序头信息
     *
     * @param header
     */
    public static void parseProgramHeaderList(byte[] header) {
        int offset = Utils.byte2Int(type_32.hdr.Program_header_off);
        //每一个大小都是 0x16-》32字节
        int header_size = 32;//32个字节
        int header_count = Utils.byte2Short(type_32.hdr.e_phnumCount);//头部的个数
        byte[] des = new byte[header_size];
        for (int i = 0; i < header_count; i++) {
            //参数
            //1,原数组
            //2,原数组开始 位置
            //3,赋值的数组
            //4,赋值的数组的开始位置
            //5,长度
            System.arraycopy(header, i * header_size + offset,
                    des, 0, header_size);
            type_32.ProgramHeaderList.add(parseProgramHeader(des));
        }
    }

    private static ElfType32.Elf32_ProgramHeeaderItem parseProgramHeader(byte[] header) {
        /**
         public int p_type;
         public int p_offset;
         public int p_vaddr;
         public int p_paddr;
         public int p_filesz;
         public int p_memsz;
         public int p_flags;
         public int p_align;
         */
        ElfType32.Elf32_ProgramHeeaderItem phdr = new ElfType32.Elf32_ProgramHeeaderItem();
        phdr.p_type = Utils.copyBytes(header, 0, 4);
        phdr.p_offset = Utils.copyBytes(header, 4, 4);
        phdr.p_vaddr = Utils.copyBytes(header, 8, 4);
        phdr.p_paddr = Utils.copyBytes(header, 12, 4);
        phdr.p_filesz = Utils.copyBytes(header, 16, 4);
        phdr.p_memsz = Utils.copyBytes(header, 20, 4);
        phdr.p_flags = Utils.copyBytes(header, 24, 4);
        phdr.p_align = Utils.copyBytes(header, 28, 4);
        return phdr;

    }




}
